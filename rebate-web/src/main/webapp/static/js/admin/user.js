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
        sui =$("#search_sui").val();
        subUnionId =$("#search_subUnionId").val();


        page =  $scope.paginationConf.currentPage;
        pageSize =  $scope.paginationConf.itemsPerPage;

        $http.get("/admin/getUserList.json",{params:{nickname: encodeURIComponent(nickname),openId:openId,subUnionId:subUnionId,sui:sui,status:status,agent:agent,page:page,pageSize:pageSize,r:Math.random()}}).success(function (response) {
            if (response.success) {
                $scope.userList = response.userList;
                $scope.paginationConf.totalItems = response.totalItem;
            }
        });
    }

    $scope.addWhiteList = function(subUnionId){

        if(!confirm("确定要进行操作吗?")){
            return;
        }

        $http.get("/admin/addWhiteAgent.json",{params:{subUnionId: subUnionId,r:Math.random()}}).success(function (response) {
            if (response.success) {
                $scope.query();
                //$("#addWhiteBtn"+subUnionId).hide();
                //$("#cancelWhiteBtn"+subUnionId).show();
            }
        });
    }

    $scope.cancelWhiteList = function(subUnionId){
        if(!confirm("确定要进行操作吗?")){
            return;
        }

        $http.get("/admin/cancelWhiteAgent.json",{params:{subUnionId: subUnionId,r:Math.random()}}).success(function (response) {
            if (response.success) {
                $scope.query();
                //$("#addWhiteBtn"+subUnionId).show();
                //$("#cancelWhiteBtn"+subUnionId).hide();
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
