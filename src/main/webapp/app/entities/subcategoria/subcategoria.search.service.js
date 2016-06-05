(function() {
    'use strict';

    angular
        .module('ecommerceApp')
        .factory('SubcategoriaSearch', SubcategoriaSearch);

    SubcategoriaSearch.$inject = ['$resource'];

    function SubcategoriaSearch($resource) {
        var resourceUrl =  'api/_search/subcategorias/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
