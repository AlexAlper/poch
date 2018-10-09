// Контроллер основного приложения
app.controller('RefCorrespondentsAppController', function($http, $location, $uibModal) {

    const demoApp = this;

    // We identify the node.
    const apiBaseURL = "/api/example/";



    demoApp.openOffer = (item) => {
        const modalInstance = $uibModal.open({
            templateUrl: 'Offer.html',
            controller: 'CreateOrEditItemCtrl',
            controllerAs: 'modalInstance',
            resolve: {
                item: () => item,
                demoApp: () =>  demoApp,
                apiBaseURL: () => apiBaseURL
            }
        });

        modalInstance.result.then(() => {}, () => {});
    };
   
     
     demoApp.openModal = (item) => {
        const modalInstance = $uibModal.open({
            templateUrl: 'createOrEditItemTemplate.html',
            controller: 'CreateOrEditItemCtrl',
            controllerAs: 'modalInstance',
            resolve: {
                item: () => item,
                demoApp: () => demoApp,
                apiBaseURL: () => apiBaseURL
            }
        });

        modalInstance.result.then(() => {}, () => {});
    };


// Отобразить сообщение
    demoApp.displayMessage = (message) => {
        const modalInstanceTwo = $uibModal.open({
            templateUrl: 'messageContent.html',
            controller: 'messageCtrl',
            controllerAs: 'modalInstanceTwo',
            resolve: { message: () => message }
        });

        // Поведение на закрытии модального окна отсутствует
        modalInstanceTwo.result.then(() => {}, () => {});
    };


demoApp.getAllItems = () => $http.get(apiBaseURL + "get-user")
       .then((response) =>
       demoApp.items = Object.keys(response.data)
                           .map((key) =>
                           {
                               var item= response.data[key];
                               if(item.offerCreate){
                                   item.itemCreate = new Date(item.itemCreate).toLocaleDateString("ru-Ru");
                               }
                               return item;
                           })
           .reverse());

    demoApp.getAllItems();

    }); 


// Контроллер для модельного окна создания/редактирования записи
app.controller('CreateOrEditItemCtrl', function ($http, $location, $uibModalInstance, $uibModal, item, demoApp, apiBaseURL) {
    const modalInstance = this;

    modalInstance.form = {};
    modalInstance.formError = false;
    modalInstance.isEditMode = false; // false - create mode

    if(item){ 
        
        //TODO: Заполнение формы при редактировании
        modalInstance.form.cor_id = item.cor_id;
        modalInstance.form.bill = item.bill;

        modalInstance.isEditMode = true;
    } else {
        modalInstance.form.cor_id = "1";
        modalInstance.form.bill = "0";
    }

    modalInstance.title = modalInstance.isEditMode ? "Редактировать элемент" : "Новый элемент";

    modalInstance.create = () => {
        if (invalidFormInput()) {
            modalInstance.formError = true;
        } else {
            modalInstance.formError = false;

            $uibModalInstance.close();
            
            const createCorrespondentEndpoint = `${apiBaseURL}create-user?cor_id=${modalInstance.form.cor_id}&bill=${modalInstance.form.bill}`;

            $http.put(createCorrespondentEndpoint).then(
                (result) => {
                    modalInstance.displayMessage(result);
                    demoApp.getAllCorrespondents();
                },
                (result) => {
                    result.data = "Ошибка создания транзакции: " + result.status +' '+ result.statusText + " [" + result.data +"]";
                    modalInstance.displayMessage(result);
                }
            );
        
        }
    };



    modalInstance.save = () => {

        if (invalidFormInput()) {
            modalInstance.formError = true;
        } else {
            modalInstance.formError = false;

       const changeCorrespondentEndpoint = `${apiBaseURL}change-user`;
        var newItem = item;
        if(newItem){
            newItem.cor_id = modalInstance.form.cor_id;
            newItem.bill = modalInstance.form.bill;

        } 




         $http.put(changeCorrespondentEndpoint, newItem).then(
            (result) => {
                modalInstance.displayMessage(result);
                demoApp.getAllItems();
                $uibModalInstance.close();
            },
            (result) => {
                result.data = "Ошибка сохранения транзакции: " + result.status +' '+ result.statusText + " [" + result.data +"]";
                modalInstance.displayMessage(result);
            }
        );
   
        }
}


    modalInstance.displayMessage = (message) => {
        const modalInstanceTwo = $uibModal.open({
            templateUrl: 'messageContent.html',
            controller: 'messageCtrl',
            controllerAs: 'modalInstanceTwo',
            resolve: { message: () => message }
        });

 
        modalInstanceTwo.result.then(() => {}, () => {});
    };


    modalInstance.cancel = () => $uibModalInstance.dismiss();


    function invalidFormInput() {
        var invalid = IsNullOrWhitespace(modalInstance.form.cor_id) || 
        IsNullOrWhitespace(modalInstance.form.bill);

        return invalid;
    }

});    