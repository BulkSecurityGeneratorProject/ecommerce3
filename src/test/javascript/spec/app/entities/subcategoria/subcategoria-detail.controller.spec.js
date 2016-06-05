'use strict';

describe('Controller Tests', function() {

    describe('Subcategoria Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockSubcategoria, MockCategoria, MockProducto;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockSubcategoria = jasmine.createSpy('MockSubcategoria');
            MockCategoria = jasmine.createSpy('MockCategoria');
            MockProducto = jasmine.createSpy('MockProducto');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'Subcategoria': MockSubcategoria,
                'Categoria': MockCategoria,
                'Producto': MockProducto
            };
            createController = function() {
                $injector.get('$controller')("SubcategoriaDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'ecommerceApp:subcategoriaUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
