package com.example.flow;

        import com.google.common.collect.Sets;
        import com.example.core.helpers.StringComparator;
        import joptsimple.internal.Strings;
        import net.corda.core.flows.FlowLogic;
        import net.corda.core.flows.FlowSession;
        import net.corda.core.identity.Party;
        import net.corda.core.node.NodeInfo;
        import net.corda.core.transactions.SignedTransaction;

        import java.util.ArrayList;
        import java.util.HashSet;
        import java.util.List;
        import java.util.Set;

public abstract class BaseFlow extends FlowLogic<SignedTransaction> {


    protected Set<Party> getAllPartiesWithoutMe() {
        final List<String> allPartiesNameOrg = new ArrayList<String>() {{
            add("PartyA");
            add("PartyB");
            add("PartyC");
            add("PartyD");
            add("PartyE");
            add("PartyF");
        }};

        Set<Party> additionalParties = new HashSet<>();
        List<NodeInfo> allNodes = getServiceHub().getNetworkMapCache().getAllNodes();
        String meOrganisation = getServiceHub().getMyInfo().getLegalIdentities().get(0).getName().getOrganisation();

        for (NodeInfo ni : allNodes) {
            for (Party p : ni.getLegalIdentities()) {

                String pOrgName = p.getName().getOrganisation();
                if (!StringComparator.IsEquals(meOrganisation, pOrgName) &&
                        allPartiesNameOrg.stream().filter(x -> StringComparator.IsEquals(pOrgName, x)).toArray().length > 0) {
                    additionalParties.add(p);
                }
            }
        }

        //return additionalParties;
        return additionalParties;
    }
}
