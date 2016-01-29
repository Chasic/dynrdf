angular.module('dynrdfApp.controllers',[]).controller('RDFObjectListController',function($scope,$state,popupService,$window,RDFObject){

    $scope.rdfobjects=RDFObject.query();


    $scope.deleteRDFObject=function(RDFObject){
        if(popupService.showPopup('Really delete this?')){
            RDFObject.$delete(function(){
                $window.location.href='';
            });
        }
    }

}).controller('RDFObjectCreatorController',function($scope,$state,$stateParams,RDFObject){

    $scope.RDFobject=new RDFObject();

    $scope.addObject=function(){
        $scope.RDFobject.$save(function(data){
            $state.go('overview');
        },
        function(data){
            alert(data.msg);
        });
    }

});
