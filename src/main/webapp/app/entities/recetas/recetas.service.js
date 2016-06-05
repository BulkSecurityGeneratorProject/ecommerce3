(function() {
    'use strict';
    angular
        .module('ecommerceApp')
        .factory('Recetas', Recetas);

    Recetas.$inject = ['$resource'];

    function Recetas ($resource) {
        var resourceUrl =  'api/recetas/:id';

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
