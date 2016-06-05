(function() {
    'use strict';

    angular
        .module('ecommerceApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('recetas', {
            parent: 'entity',
            url: '/recetas?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'ecommerceApp.recetas.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/recetas/recetas.html',
                    controller: 'RecetasController',
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
                    $translatePartialLoader.addPart('recetas');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('recetas-detail', {
            parent: 'entity',
            url: '/recetas/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'ecommerceApp.recetas.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/recetas/recetas-detail.html',
                    controller: 'RecetasDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('recetas');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Recetas', function($stateParams, Recetas) {
                    return Recetas.get({id : $stateParams.id}).$promise;
                }]
            }
        })
        .state('recetas.new', {
            parent: 'recetas',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/recetas/recetas-dialog.html',
                    controller: 'RecetasDialogController',
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
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('recetas', null, { reload: true });
                }, function() {
                    $state.go('recetas');
                });
            }]
        })
        .state('recetas.edit', {
            parent: 'recetas',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/recetas/recetas-dialog.html',
                    controller: 'RecetasDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Recetas', function(Recetas) {
                            return Recetas.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('recetas', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('recetas.delete', {
            parent: 'recetas',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/recetas/recetas-delete-dialog.html',
                    controller: 'RecetasDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Recetas', function(Recetas) {
                            return Recetas.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('recetas', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
