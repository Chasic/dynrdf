angular.module('dynrdfApp.controllers',[]).controller('RDFObjectListController',function($scope,$state,popupService,$window,RDFObject){



}).controller('OverviewController',function($scope,$state,$stateParams,RDFObject, popupService){

    $scope.rdfobjects=RDFObject.query();
    $scope.RDFobject=new RDFObject();


    $scope.deleteRDFObject=function(RDFObject){
        RDFObject.id = RDFObject.fullName;
        toHide = RDFObject.fullName;
        if(popupService.showPopup('Really delete this?')){
            var index = -1;
            var objArr = eval( $scope.rdfobjects );
            for( var i = 0; i < objArr.length; i++ ) {
                if( objArr[i].fullName === toHide ) {
                    index = i;
                    break;
                }
            }

            $scope.rdfobjects.splice( index, 1 );
            RDFObject.$delete(function(){
               // window.location.href='';
            });
        }
    };


}).controller('RDFObjectEntityController',function($scope,$state,$stateParams,RDFObject){

    var loadedObjVendor;
    var loadedObjName;
    if (typeof $stateParams.id !== 'undefined') {

        $scope.RDFobject = RDFObject.get({ id: $stateParams.id },function(data) {
            $scope.RDFobject = data;
            $scope.showCustomData();

            loadedObjVendor = $scope.RDFobject.vendor;
            loadedObjName = $scope.RDFobject.name;

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
        $scope.RDFobject.id = loadedObjVendor +"/"+ loadedObjName;
        $scope.RDFobject.$update(function(data){
                $state.go('overview');
            },
            function(data){
                alert(angular.fromJson(data).data.msg);
            });
    }

    $scope.showCustomData=function(){
        if($scope.RDFobject.type == "PROXY"){ //proxy
            $scope.proxyState = true;
        }
        else{
            $scope.proxyState = false;
        }

        if($scope.RDFobject.type == "SPARQL-ENDPOINT"){ //sparql endpoint
            $scope.endpointState = true;
        }
        else{
            $scope.endpointState = false;
        }
    }

});
