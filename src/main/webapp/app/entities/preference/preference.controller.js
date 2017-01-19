(function() {
    'use strict';

    angular
        .module('21PointsApp')
        .controller('PreferenceController', PreferenceController);

    PreferenceController.$inject = ['$scope', '$state', 'Preference', 'PreferenceSearch'];

    function PreferenceController ($scope, $state, Preference, PreferenceSearch) {
        var vm = this;

        vm.preferences = [];
        vm.clear = clear;
        vm.search = search;
        vm.loadAll = loadAll;

        loadAll();

        function loadAll() {
            Preference.query(function(result) {
                vm.preferences = result;
                vm.searchQuery = null;
            });
        }

        function search() {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            PreferenceSearch.query({query: vm.searchQuery}, function(result) {
                vm.preferences = result;
                vm.currentSearch = vm.searchQuery;
            });
        }

        function clear() {
            vm.searchQuery = null;
            loadAll();
        }    }
})();
