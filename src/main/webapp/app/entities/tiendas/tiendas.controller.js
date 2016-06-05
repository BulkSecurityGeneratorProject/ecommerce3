(function() {
    'use strict';

    angular
        .module('ecommerceApp')
        .controller('TiendasController', TiendasController);

    TiendasController.$inject = ['$scope', '$state', 'DataUtils', 'Tiendas', 'TiendasSearch'];

    function TiendasController ($scope, $state, DataUtils, Tiendas, TiendasSearch) {
        var vm = this;
        
        vm.tiendas = [];
        vm.openFile = DataUtils.openFile;
        vm.byteSize = DataUtils.byteSize;
        vm.search = search;

        loadAll();

        function loadAll() {
            Tiendas.query(function(result) {
                vm.tiendas = result;
            });
        }

        function search () {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            TiendasSearch.query({query: vm.searchQuery}, function(result) {
                vm.tiendas = result;
            });
        }    }
})();
