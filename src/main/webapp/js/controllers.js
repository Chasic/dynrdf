angular.module('dynrdfApp.controllers',[]).controller('RDFObjectListController',function($scope,$state,popupService,$window,RDFObject){



}).controller('OverviewController',function($scope,$state,$stateParams,RDFObject, popupService){

    $scope.rdfobjects=RDFObject.query();
    $scope.RDFobject=new RDFObject();


    $scope.deleteRDFObject=function(RDFObject){
        if(popupService.showPopup('Really delete this?')){
            RDFObject.$delete(function(){
                $window.location.href='';
            });
        }
    };


}).controller('RDFObjectEntityController',function($scope,$state,$stateParams,RDFObject){


    if (typeof $stateParams.id !== 'undefined') {

        $scope.RDFobject = RDFObject.get({ id: $stateParams.id },function(data) {
            $scope.RDFobject = data;
        });
    }else{
        $scope.RDFobject=new RDFObject();
    }


    $scope.addObject=function(){
        $scope.RDFobject.$save(function(data){
                $state.go('overview');
            },
            function(data){
                alert(data.msg);
            });
    }

    $scope.editObject=function(RDFObject){
        $scope.RDFobject.$update(function(data){
                $state.go('overview');
            },
            function(data){
                alert(data.msg);
            });
    }

});
