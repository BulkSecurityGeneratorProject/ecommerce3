(function() {
    'use strict';

    angular
        .module('ecommerceApp')
        .controller('RecetasDeleteController',RecetasDeleteController);

    RecetasDeleteController.$inject = ['$uibModalInstance', 'entity', 'Recetas'];

    function RecetasDeleteController($uibModalInstance, entity, Recetas) {
        var vm = this;

        vm.recetas = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Recetas.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
