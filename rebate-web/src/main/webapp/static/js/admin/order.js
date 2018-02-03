adminModule.controller("orderController", ["$scope", "$http", function ($scope, $http) {
    $scope.search_status='';
    $scope.paginationConf = {
        currentPage: 1,
        totalItems: 0,
        itemsPerPage: 15,
        pagesLength: 5,
        perPageOptions: [10, 20, 30, 40, 50],
        onChange: function(){
            $scope.query();
        }
    };

    $scope.query = function () {
        startDate= $("#search_startDate").val();
        endDate= $("#search_endDate").val();
        status= $("#search_status").val();
        validCode= $("#search_validCode").val();
        subUnionId =$("#search_subUnionId").val();
        orderId= $("#search_orderId").val();
        page =  $scope.paginationConf.currentPage;
        pageSize =  $scope.paginationConf.itemsPerPage;

        $http.get("/admin/orders.json",{params:{startDate:startDate,endDate:endDate,validCode:validCode,orderId:orderId,subUnionId:subUnionId,status:status,page:page,pageSize:pageSize,r:Math.random()}}).success(function (response) {
            if (response.success) {
                $scope.detailList = response.detailList;
                $scope.paginationConf.totalItems = response.totalItem;

            }
        });
    }


    $scope.selectAll = function(event){
        var action = event.target;
        var groupCheckbox=$("input[name='ckb']");
        if(action.checked){
            //全部选中
            for(i=0;i<groupCheckbox.length;i++){
                groupCheckbox[i].checked = true;
            }
        }else{
            //取消选中
            for(i=0;i<groupCheckbox.length;i++){
                groupCheckbox[i].checked = false;
            }
        }
    }


    $scope.query();

}]);