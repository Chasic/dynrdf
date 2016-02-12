var REST_SERVICE = "http://localhost:8080/bakalarka_war_exploded/api/";
REST_SERVICE = "http://127.0.0.1:8080/dynrdf/api/";

angular.module('dynrdfApp.services',[]).factory('RDFObject',function($resource){
    return $resource(REST_SERVICE + 'objects/:id',{id:'@id'},{
        update: {
            method: 'PUT'
        }
    }
    );
}).service('popupService',function($window){
    this.showPopup=function(message){
        return $window.confirm(message);
    }
});