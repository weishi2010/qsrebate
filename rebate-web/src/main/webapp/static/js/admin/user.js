adminModule.controller("userController", ["$scope", "$http", function ($scope, $http) {
    $scope.search_systemName='';
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
        openId= $("#search_openId").val();
        nickname =$("#search_nickname").val();
        agent =$("#search_agent").val();
        status =$("#search_status").val();

        page =  $scope.paginationConf.currentPage;
        pageSize =  $scope.paginationConf.itemsPerPage;

        $http.get("/admin/getUserList.json",{params:{nickname: encodeURIComponent(nickname),openId:openId,status:status,agent:agent,page:page,pageSize:pageSize,r:Math.random()}}).success(function (response) {
            if (response.success) {
                $scope.userList = response.userList;
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
