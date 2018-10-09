"use strict";

const app = angular.module('demoAppModule', ['ui.bootstrap']);

// Fix for unhandled rejections bug.
app.config(['$qProvider', function ($qProvider) {
    $qProvider.errorOnUnhandledRejections(false);
}]);

// Контроллер основного приложения
app.controller('DemoAppController', function($http, $location, $uibModal) {
    const demoApp = this;

    // We identify the node.
    const apiBaseURL = "/api/example/";

    $http.get(apiBaseURL + "me").then((response) => demoApp.thisNode = response.data.me);

    // Отобразить сообщение 
    modalInstance.displayMessage = (message) => {
        const modalInstanceTwo = $uibModal.open({
            templateUrl: 'messageContent.html',
            controller: 'messageCtrl',
            controllerAs: 'modalInstanceTwo',
            resolve: { message: () => message }
        });

        // Поведение на закрытии модального окна отсутствует
        modalInstanceTwo.result.then(() => {}, () => {});
    };

    // Закрыть диалоговое окно
    modalInstance.cancel = () => $uibModalInstance.dismiss();

    // Валидирует форму 
    // Возвращает True в случае наличия ошибок и False в противном случае
    function invalidFormInput() {
        var invalid = IsNullOrWhitespace(modalInstance.form.docNumber) ||
        IsNullOrWhitespace(modalInstance.form.correspondent) ||
        IsNullOrWhitespace(modalInstance.form.content);

        return invalid;
    }
});

// Сообщение 
app.controller('messageCtrl', function ($uibModalInstance, message) {
    const modalInstanceTwo = this;
    modalInstanceTwo.message = message.data;
});

/// Проверяет, что строка не задана
    function IsNullOrWhitespace(input) {
        return (typeof input === 'undefined' || input == null) // || isNaN(input))
            || ('' + input).replace(/\s/g, '').length < 1;
    }