(function() {
    'use strict';

    angular
        .module('ecommerceApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('producto', {
            parent: 'entity',
            url: '/producto?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'ecommerceApp.producto.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/producto/productos.html',
                    controller: 'ProductoController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'id,asc',
                    squash: true
                },
                search: null
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        search: $stateParams.search
                    };
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('producto');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
       /* .state('Asist', {
            parent: 'entity',
            url: '/jugadors/{Asist}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'pruebapractica2App.jugador.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'scripts/app/entities/jugador/jugadorsTabla2.html',
                    controller: 'AsistController'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('jugador');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Jugador', function($stateParams, Jugador) {
                    return Jugador.Asist({totalAsist : 20});
                }]
            }
        })*/
        .state('producto-detail', {
            parent: 'entity',
            url: '/producto/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'ecommerceApp.producto.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/producto/producto-detail.html',
                    controller: 'ProductoDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('producto');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Producto', function($stateParams, Producto) {
                    return Producto.get({id : $stateParams.id}).$promise;
                }]
            }
        })
        .state('producto.new', {
            parent: 'producto',
            url: '/new',
            data: {
                authorities: ['ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/producto/producto-dialog.html',
                    controller: 'ProductoDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                nombre: null,
                                descripcion: null,
                                imagen: null,
                                imagenContentType: null,
                                precio: null,
                                cantidad: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('producto', null, { reload: true });
                }, function() {
                    $state.go('producto');
                });
            }]
        })
        .state('producto.edit', {
            parent: 'producto',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/producto/producto-dialog.html',
                    controller: 'ProductoDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Producto', function(Producto) {
                            return Producto.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('producto', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('producto.delete', {
            parent: 'producto',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/producto/producto-delete-dialog.html',
                    controller: 'ProductoDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Producto', function(Producto) {
                            return Producto.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('producto', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
