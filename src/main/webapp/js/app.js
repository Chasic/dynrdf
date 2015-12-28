

angular.module('dynrdfApp',['ui.router','ngResource','dynrdfApp.controllers','dynrdfApp.services']);

angular.module('dynrdfApp').config(function($stateProvider,$httpProvider){
    $stateProvider.state('overview',{
        url:'/overview',
        templateUrl:'partials/objects.html',
        controller:'RDFObjectListController'
    }).state('newObject',{
       url:'/create',
       templateUrl:'partials/add_object.html',
       controller:'RDFObjectCreatorController'
    });
}).run(function($state){
   $state.go('overview');
});