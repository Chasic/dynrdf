var REST_SERVICE = "";

rest_service = String( window.location );
if( rest_service.match(".*#.*") ){
    index = rest_service.indexOf("#");
    REST_SERVICE = rest_service.substr(0, index) + "api/";
}
else{
    REST_SERVICE = rest_service + "api/";
}

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