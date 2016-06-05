(function() {
    'use strict';

    angular
        .module('ecommerceApp')
        .controller('TiendasDeleteController',TiendasDeleteController);

    TiendasDeleteController.$inject = ['$uibModalInstance', 'entity', 'Tiendas'];

    function TiendasDeleteController($uibModalInstance, entity, Tiendas) {
        var vm = this;

        vm.tiendas = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Tiendas.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
