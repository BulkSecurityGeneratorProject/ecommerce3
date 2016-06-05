(function() {
    'use strict';

    angular
        .module('ecommerceApp')
        .factory('CategoriaSearch', CategoriaSearch);

    CategoriaSearch.$inject = ['$resource'];

    function CategoriaSearch($resource) {
        var resourceUrl =  'api/_search/categorias/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
