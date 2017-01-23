(function() {
    'use strict';

    angular
        .module('21PointsApp')
        .controller('PreferenceDeleteController',PreferenceDeleteController);

    PreferenceDeleteController.$inject = ['$uibModalInstance', 'entity', 'Preference'];

    function PreferenceDeleteController($uibModalInstance, entity, Preference) {
        var vm = this;

        vm.preference = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Preference.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
