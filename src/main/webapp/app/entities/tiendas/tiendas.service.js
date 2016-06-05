(function() {
    'use strict';
    angular
        .module('ecommerceApp')
        .factory('Tiendas', Tiendas);

    Tiendas.$inject = ['$resource'];

    function Tiendas ($resource) {
        var resourceUrl =  'api/tiendas/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
