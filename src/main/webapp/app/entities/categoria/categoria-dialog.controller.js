(function() {
    'use strict';

    angular
        .module('ecommerceApp')
        .controller('CategoriaDialogController', CategoriaDialogController);

    CategoriaDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Categoria', 'Subcategoria'];

    function CategoriaDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Categoria, Subcategoria) {
        var vm = this;

        vm.categoria = entity;
        vm.clear = clear;
        vm.save = save;
        vm.subcategorias = Subcategoria.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.categoria.id !== null) {
                Categoria.update(vm.categoria, onSaveSuccess, onSaveError);
            } else {
                Categoria.save(vm.categoria, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('ecommerceApp:categoriaUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
