(function() {
    'use strict';

    angular
        .module('ecommerceApp')
        .controller('RecetasDetailController', RecetasDetailController);

    RecetasDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'DataUtils', 'entity', 'Recetas'];

    function RecetasDetailController($scope, $rootScope, $stateParams, DataUtils, entity, Recetas) {
        var vm = this;

        vm.recetas = entity;
        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;

        var unsubscribe = $rootScope.$on('ecommerceApp:recetasUpdate', function(event, result) {
            vm.recetas = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
