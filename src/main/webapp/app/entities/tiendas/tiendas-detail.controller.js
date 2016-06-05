(function() {
    'use strict';

    angular
        .module('ecommerceApp')
        .controller('TiendasDetailController', TiendasDetailController);

    TiendasDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'DataUtils', 'entity', 'Tiendas'];

    function TiendasDetailController($scope, $rootScope, $stateParams, DataUtils, entity, Tiendas) {
        var vm = this;

        vm.tiendas = entity;
        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;

        var unsubscribe = $rootScope.$on('ecommerceApp:tiendasUpdate', function(event, result) {
            vm.tiendas = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
