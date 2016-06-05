(function() {
    'use strict';

    angular
        .module('ecommerceApp')
        .controller('MarcaDialogController', MarcaDialogController);

    MarcaDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Marca', 'Producto'];

    function MarcaDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Marca, Producto) {
        var vm = this;

        vm.marca = entity;
        vm.clear = clear;
        vm.save = save;
        vm.productos = Producto.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.marca.id !== null) {
                Marca.update(vm.marca, onSaveSuccess, onSaveError);
            } else {
                Marca.save(vm.marca, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('ecommerceApp:marcaUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
