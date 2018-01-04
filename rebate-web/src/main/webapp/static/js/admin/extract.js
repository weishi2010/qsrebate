adminModule.controller("extractController", ["$scope", "$http", function ($scope, $http) {
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
        status= $("#search_status").val();
        openId =$("#search_openId").val();

        page =  $scope.paginationConf.currentPage;
        pageSize =  $scope.paginationConf.itemsPerPage;

        $http.get("/admin/getExtractDetails.json",{params:{openId:openId,status:status,page:page,pageSize:pageSize,r:Math.random()}}).success(function (response) {
            if (response.success) {
                $scope.extractList = response.extractList;
                $scope.paginationConf.totalItems = response.totalItem;

            }
        });
    }

    $scope.updateStatus = function (openId,id) {
        if(!confirm("确定要更新为已结算?")){
            return;
        }

        $http.get("/admin/updateExtractDetail.json",{params:{openId:openId,id:id,r:Math.random()}}).success(function (response) {
            if (response.success) {
                $scope.query();
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
