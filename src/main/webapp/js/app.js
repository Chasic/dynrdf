

angular.module('dynrdfApp',['ui.router','ngResource','dynrdfApp.controllers','dynrdfApp.services']);

angular.module('dynrdfApp').config(function($stateProvider,$httpProvider){
    $stateProvider.state('overview',{
        url:'/overview',
        templateUrl:'partials/objects.html',
        controller:'OverviewController'
    }).state('newObject',{
        url:'/create',
        templateUrl:'partials/add_object.html',
        controller:'RDFObjectEntityController'
    }).state('viewObject',{
        url:'/object/:id',
        templateUrl:'partials/edit_object.html',
        controller:'RDFObjectEntityController',

    });
}).run(function($state){
   $state.go('overview');
});