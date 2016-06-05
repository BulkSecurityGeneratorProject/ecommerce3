(function() {
    'use strict';

    angular
        .module('ecommerceApp')
        .controller('MarcaDetailController', MarcaDetailController);

    MarcaDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'Marca', 'Producto'];

    function MarcaDetailController($scope, $rootScope, $stateParams, entity, Marca, Producto) {
        var vm = this;

        vm.marca = entity;

        var unsubscribe = $rootScope.$on('ecommerceApp:marcaUpdate', function(event, result) {
            vm.marca = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
