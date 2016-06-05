(function() {
    'use strict';

    angular
        .module('ecommerceApp')
        .controller('CategoriaDetailController', CategoriaDetailController);

    CategoriaDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'Categoria', 'Subcategoria'];

    function CategoriaDetailController($scope, $rootScope, $stateParams, entity, Categoria, Subcategoria) {
        var vm = this;

        vm.categoria = entity;

        var unsubscribe = $rootScope.$on('ecommerceApp:categoriaUpdate', function(event, result) {
            vm.categoria = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
