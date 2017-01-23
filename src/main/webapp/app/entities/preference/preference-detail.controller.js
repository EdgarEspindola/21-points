(function() {
    'use strict';

    angular
        .module('21PointsApp')
        .controller('PreferenceDetailController', PreferenceDetailController);

    PreferenceDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Preference', 'User'];

    function PreferenceDetailController($scope, $rootScope, $stateParams, previousState, entity, Preference, User) {
        var vm = this;

        vm.preference = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('21PointsApp:preferenceUpdate', function(event, result) {
            vm.preference = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
