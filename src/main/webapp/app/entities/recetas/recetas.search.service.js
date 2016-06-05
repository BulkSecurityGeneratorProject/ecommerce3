(function() {
    'use strict';

    angular
        .module('ecommerceApp')
        .factory('RecetasSearch', RecetasSearch);

    RecetasSearch.$inject = ['$resource'];

    function RecetasSearch($resource) {
        var resourceUrl =  'api/_search/recetas/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
