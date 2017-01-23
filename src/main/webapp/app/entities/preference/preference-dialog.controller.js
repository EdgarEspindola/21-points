(function() {
    'use strict';

    angular
        .module('21PointsApp')
        .controller('PreferenceDialogController', PreferenceDialogController);

    PreferenceDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', '$q', 'entity', 'Preference', 'User'];

    function PreferenceDialogController ($timeout, $scope, $stateParams, $uibModalInstance, $q, entity, Preference, User) {
        var vm = this;

        vm.preference = entity;
        vm.clear = clear;
        vm.save = save;
        vm.users = User.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.preference.id !== null) {
                Preference.update(vm.preference, onSaveSuccess, onSaveError);
            } else {
                Preference.save(vm.preference, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('21PointsApp:preferenceUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
