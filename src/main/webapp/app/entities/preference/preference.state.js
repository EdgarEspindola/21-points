(function() {
    'use strict';

    angular
        .module('21PointsApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('preference', {
            parent: 'entity',
            url: '/preference',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: '21PointsApp.preference.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/preference/preferences.html',
                    controller: 'PreferenceController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('preference');
                    $translatePartialLoader.addPart('units');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('preference-detail', {
            parent: 'entity',
            url: '/preference/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: '21PointsApp.preference.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/preference/preference-detail.html',
                    controller: 'PreferenceDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('preference');
                    $translatePartialLoader.addPart('units');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Preference', function($stateParams, Preference) {
                    return Preference.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'preference',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('preference-detail.edit', {
            parent: 'preference-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/preference/preference-dialog.html',
                    controller: 'PreferenceDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Preference', function(Preference) {
                            return Preference.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('preference.new', {
            parent: 'preference',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/preference/preference-dialog.html',
                    controller: 'PreferenceDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                weeklyGoal: null,
                                weightUnits: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('preference', null, { reload: 'preference' });
                }, function() {
                    $state.go('preference');
                });
            }]
        })
        .state('preference.edit', {
            parent: 'preference',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/preference/preference-dialog.html',
                    controller: 'PreferenceDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Preference', function(Preference) {
                            return Preference.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('preference', null, { reload: 'preference' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('preference.delete', {
            parent: 'preference',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/preference/preference-delete-dialog.html',
                    controller: 'PreferenceDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Preference', function(Preference) {
                            return Preference.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('preference', null, { reload: 'preference' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
