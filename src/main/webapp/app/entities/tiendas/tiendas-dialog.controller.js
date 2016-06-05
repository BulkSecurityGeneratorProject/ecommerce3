(function() {
    'use strict';

    angular
        .module('ecommerceApp')
        .controller('TiendasDialogController', TiendasDialogController);

    TiendasDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'DataUtils', 'entity', 'Tiendas'];

    function TiendasDialogController ($timeout, $scope, $stateParams, $uibModalInstance, DataUtils, entity, Tiendas) {
        var vm = this;

        vm.tiendas = entity;
        vm.clear = clear;
        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;
        vm.save = save;

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.tiendas.id !== null) {
                Tiendas.update(vm.tiendas, onSaveSuccess, onSaveError);
            } else {
                Tiendas.save(vm.tiendas, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('ecommerceApp:tiendasUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
