(function() {
    'use strict';

    angular
        .module('ecommerceApp')
        .controller('RecetasDialogController', RecetasDialogController);

    RecetasDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'DataUtils', 'entity', 'Recetas'];

    function RecetasDialogController ($timeout, $scope, $stateParams, $uibModalInstance, DataUtils, entity, Recetas) {
        var vm = this;

        vm.recetas = entity;
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
            if (vm.recetas.id !== null) {
                Recetas.update(vm.recetas, onSaveSuccess, onSaveError);
            } else {
                Recetas.save(vm.recetas, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('ecommerceApp:recetasUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


        vm.setImagen = function ($file, recetas) {
            if ($file && $file.$error === 'pattern') {
                return;
            }
            if ($file) {
                DataUtils.toBase64($file, function(base64Data) {
                    $scope.$apply(function() {
                        recetas.imagen = base64Data;
                        recetas.imagenContentType = $file.type;
                    });
                });
            }
        };

    }
})();
