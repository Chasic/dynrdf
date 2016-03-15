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
            $scope.showCustomData();
        });
    }else{
        $scope.RDFobject=new RDFObject();
        $scope.RDFobject.priority = 1;
        $scope.RDFobject.proxyUrl = "http://";
        $scope.RDFobject.proxyParam = "url";
    }


    $scope.addObject=function(){
        $scope.RDFobject.$save(function(data){
                $state.go('overview');
            },
            function(data){
                alert(angular.fromJson(data).data.msg);
            });
    }

    $scope.editObject=function(RDFObject){
        $scope.RDFobject.$update(function(data){
                $state.go('overview');
            },
            function(data){
                alert(angular.fromJson(data).data.msg);
            });
    }

    $scope.showCustomData=function(){
        if($scope.RDFobject.type == 6){ //proxy
            $scope.proxyState = true;
        }
        else{
            $scope.proxyState = false;
        }

        if($scope.RDFobject.type == 5){ //sparql endpoint
            $scope.endpointState = true;
        }
        else{
            $scope.endpointState = false;
        }
    }

});
