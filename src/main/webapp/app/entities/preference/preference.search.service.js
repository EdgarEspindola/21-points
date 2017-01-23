(function() {
    'use strict';

    angular
        .module('21PointsApp')
        .factory('PreferenceSearch', PreferenceSearch);

    PreferenceSearch.$inject = ['$resource'];

    function PreferenceSearch($resource) {
        var resourceUrl =  'api/_search/preferences/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
