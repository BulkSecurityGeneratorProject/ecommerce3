(function() {
    'use strict';

    angular
        .module('ecommerceApp')
        .controller('SubcategoriaDetailController', SubcategoriaDetailController);

    SubcategoriaDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'Subcategoria', 'Categoria', 'Producto'];

    function SubcategoriaDetailController($scope, $rootScope, $stateParams, entity, Subcategoria, Categoria, Producto) {
        var vm = this;

        vm.subcategoria = entity;

        var unsubscribe = $rootScope.$on('ecommerceApp:subcategoriaUpdate', function(event, result) {
            vm.subcategoria = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
