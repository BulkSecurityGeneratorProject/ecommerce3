(function() {
    'use strict';

    angular
        .module('ecommerceApp')
        .factory('TiendasSearch', TiendasSearch);

    TiendasSearch.$inject = ['$resource'];

    function TiendasSearch($resource) {
        var resourceUrl =  'api/_search/tiendas/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
