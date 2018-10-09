package com.example.user;

import co.paralleluniverse.fibers.Suspendable;
import com.example.core.helpers.Commands;
import com.example.flow.BaseFlow;
import net.corda.core.contracts.*;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.identity.PartyAndCertificate;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.QueryCriteria;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;

import java.security.PublicKey;
import java.util.*;

import static net.corda.core.contracts.ContractsDSL.requireThat;



public abstract class EditFlowUser {

    /**
     * Изменение документа
     */
    @InitiatingFlow
    @StartableByRPC
    public static class Initiator extends BaseFlow {

        //region Getters
        public UniqueIdentifier getLinearId() {
            return linearId;
        }

        public String getCor_id() {
            return cor_id;
        }

        public String getBill() {
            return bill;
        }


        private final UniqueIdentifier linearId;
        private final String cor_id;
        private final String bill;


        private final ProgressTracker.Step GET_INPUT_STATES = new ProgressTracker.Step("Get input states");
        private final ProgressTracker.Step GENERATING_TRANSACTION = new ProgressTracker.Step("Generating transaction based on new IOU.");
        private final ProgressTracker.Step VERIFYING_TRANSACTION = new ProgressTracker.Step("Verifying contract constraints.");
        private final ProgressTracker.Step SIGNING_TRANSACTION = new ProgressTracker.Step("Signing transaction with our private key.");
        private final ProgressTracker.Step GATHERING_SIGS = new ProgressTracker.Step("Gathering the counterparty's signature.") {
            @Override
            public ProgressTracker childProgressTracker() {
                return CollectSignaturesFlow.Companion.tracker();
            }
        };
        private final ProgressTracker.Step FINALISING_TRANSACTION = new ProgressTracker.Step("Obtaining notary signature and recording transaction.") {
            @Override
            public ProgressTracker childProgressTracker() {
                return FinalityFlow.Companion.tracker();
            }
        };

        // The progress tracker checkpoints each stage of the flow and outputs the specified messages when each
        // checkpoint is reached in the code. See the 'progressTracker.currentStep' expressions within the call()
        // function.
        private final ProgressTracker progressTracker = new ProgressTracker(
                GET_INPUT_STATES,
                GENERATING_TRANSACTION,
                VERIFYING_TRANSACTION,
                SIGNING_TRANSACTION,
                GATHERING_SIGS,
                FINALISING_TRANSACTION
        );

        public Initiator(String cor_id, String bill,  UniqueIdentifier linearId) {
            this.linearId = linearId;
            this.cor_id = cor_id;
            this.bill = bill;

        }

        @Override
        @Suspendable
        public SignedTransaction call() throws FlowException {

            Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);
            PartyAndCertificate partyAndCertificate = getServiceHub().getMyInfo().getLegalIdentitiesAndCerts().get(0);
            Party currentParty = partyAndCertificate.getParty();
            PublicKey ourPubKey = partyAndCertificate.getOwningKey();
            Set<Party> otherParties = this.getAllPartiesWithoutMe();

            // Шаг 1
            progressTracker.setCurrentStep(GET_INPUT_STATES);
            // InputState: Получить предыдущее состояние, используя LinearState.linearId
            // Использовать её на вход транзакции
            QueryCriteria.LinearStateQueryCriteria linearCriteria =
                    new QueryCriteria.LinearStateQueryCriteria(Arrays.asList(currentParty),
                            Arrays.asList(this.linearId.getId()));
            Vault.Page<StateUser> results = getServiceHub().getVaultService().queryBy(StateUser.class, linearCriteria);
            StateAndRef<StateUser> inputState = results.getStates().get(0);

            // Шаг 2
            progressTracker.setCurrentStep(GENERATING_TRANSACTION);
            // OutputState: Создать выходное состояние и использовать её как
            Set<Party> allParties = new HashSet<>();
            allParties.add(currentParty);
            allParties.addAll(otherParties);

            StateUser newDocState = new StateUser(cor_id, bill, allParties, linearId);

            // Contract: создаём контакт и объединяем с выходным состоянием
            StateAndContract outputState = new StateAndContract(newDocState, ContractUser.DOC_CONTRACT_ID);


            // Command: создаём команду (для хранения публичных ключей)
            Commands.Edit commandData = new Commands.Edit();
            List<PublicKey> requiredSigners = Arrays.asList(ourPubKey);
            Command<Commands.Edit> changeDocCommand =
                    new Command<>(commandData, requiredSigners);

            // CreateTransaction: Создание транзакции
            TransactionBuilder txBuilder = new TransactionBuilder(notary);
            txBuilder.withItems(
                    inputState,
                    outputState,
                    changeDocCommand);

            // Шаг 3
            progressTracker.setCurrentStep(VERIFYING_TRANSACTION);
            // Verify: Проверка валидности транзакции
            txBuilder.verify(getServiceHub());

            // Шаг 4
            progressTracker.setCurrentStep(SIGNING_TRANSACTION);
            // Sign: подписываем транзакцию
            final SignedTransaction partialSignedTx = getServiceHub().signInitialTransaction(txBuilder);

            Set<FlowSession> flowSessions = new HashSet<>();

            for (Party otherParty : otherParties) {
                FlowSession otherPartySession = initiateFlow(otherParty);
                flowSessions.add(otherPartySession);
            }

            // Шаг 5
            progressTracker.setCurrentStep(GATHERING_SIGS);
            // Сбор подписей
            final SignedTransaction fullySignedTx = subFlow(
                    new CollectSignaturesFlow(partialSignedTx, flowSessions, CollectSignaturesFlow.Companion.tracker()));

            // Шаг 6
            progressTracker.setCurrentStep(FINALISING_TRANSACTION);
            return subFlow(new FinalityFlow(fullySignedTx, FINALISING_TRANSACTION.childProgressTracker()));

            // Если нужно переслать данную транзакцию другим участникам сети, не принимающих участие в текущией транзакции
//            val additionalParties: Set<Party> = setOf(regulator)
//            val notarisedTx2: SignedTransaction = subFlow(FinalityFlow(fullySignedTx, additionalParties, FINALISATION.childProgressTracker()))
        }

        @Override
        public ProgressTracker getProgressTracker() {
            return progressTracker;
        }
    }

    @InitiatedBy(Initiator.class)
    public static class Acceptor extends FlowLogic<SignedTransaction> {

        private final FlowSession otherPartyFlow;

        public Acceptor(FlowSession otherPartyFlow) {
            this.otherPartyFlow = otherPartyFlow;
        }

        @Suspendable
        @Override
        public SignedTransaction call() throws FlowException {
            class SignTxFlow extends SignTransactionFlow {
                private SignTxFlow(FlowSession otherPartyFlow, ProgressTracker progressTracker) {
                    super(otherPartyFlow, progressTracker);
                }

                @Override
                protected void checkTransaction(SignedTransaction stx) {
                    requireThat(require -> {
                        ContractState output = stx.getTx().getOutputs().get(0).getData();
                        require.using("This must be an DocState transaction.", output instanceof StateUser);
                        return null;
                    });
                }
            }

            return subFlow(new SignTxFlow(otherPartyFlow, SignTransactionFlow.Companion.tracker()));
        }
    }
}