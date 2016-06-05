(function() {
    'use strict';

    angular
        .module('ecommerceApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('tiendas', {
            parent: 'entity',
            url: '/tiendas',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'ecommerceApp.tiendas.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/tiendas/tiendas.html',
                    controller: 'TiendasController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('tiendas');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('tiendas-detail', {
            parent: 'entity',
            url: '/tiendas/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'ecommerceApp.tiendas.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/tiendas/tiendas-detail.html',
                    controller: 'TiendasDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('tiendas');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Tiendas', function($stateParams, Tiendas) {
                    return Tiendas.get({id : $stateParams.id}).$promise;
                }]
            }
        })
        .state('tiendas.new', {
            parent: 'tiendas',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/tiendas/tiendas-dialog.html',
                    controller: 'TiendasDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                nombre: null,
                                direccion: null,
                                telefono: null,
                                horario: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('tiendas', null, { reload: true });
                }, function() {
                    $state.go('tiendas');
                });
            }]
        })
        .state('tiendas.edit', {
            parent: 'tiendas',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/tiendas/tiendas-dialog.html',
                    controller: 'TiendasDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Tiendas', function(Tiendas) {
                            return Tiendas.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('tiendas', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('tiendas.delete', {
            parent: 'tiendas',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/tiendas/tiendas-delete-dialog.html',
                    controller: 'TiendasDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Tiendas', function(Tiendas) {
                            return Tiendas.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('tiendas', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
