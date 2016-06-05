(function() {
    'use strict';

    angular
        .module('ecommerceApp')
        .controller('SubcategoriaDialogController', SubcategoriaDialogController);

    SubcategoriaDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Subcategoria', 'Categoria', 'Producto'];

    function SubcategoriaDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Subcategoria, Categoria, Producto) {
        var vm = this;

        vm.subcategoria = entity;
        vm.clear = clear;
        vm.save = save;
        vm.categorias = Categoria.query();
        vm.productos = Producto.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.subcategoria.id !== null) {
                Subcategoria.update(vm.subcategoria, onSaveSuccess, onSaveError);
            } else {
                Subcategoria.save(vm.subcategoria, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('ecommerceApp:subcategoriaUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
