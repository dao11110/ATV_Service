<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<style>
  .nav-sidebar .menu-open>.nav-link i.right {
    -webkit-transform: rotate(90deg);
    transform: rotate(90deg);
  }

  .icon-color {
    color: #FAB34C;
    font-size: 15px !important;
  }
</style>
<!-- Main Sidebar Container -->
<aside class="main-sidebar sidebar-light-primary elevation-4" style="background: #2f303bbb;">
  <!-- Brand Logo -->
  <img src="/gsiot/assets/images/logoGuidingSystem.PNG" style="margin: 5px;" width="35px;" alt="AdminLTE Logo"
    class="brand-image img-circle elevation-3" style="opacity: .8">
  <!-- <span class="brand-text font-weight-light">GUIDING</span> -->
  <!-- Sidebar -->
  <div class="sidebar" style="margin-top: 5px; padding: 0px !important; border-top: 1px solid #ffffff;">
    <!-- Sidebar Menu -->
    <nav class="mt-2" id="itemsidebar" >
       <ul class="nav nav-pills nav-sidebar flex-column nav-child-indent" data-widget="treeview" role="menu"
        data-accordion="true">

        <li class="nav-item has-treeview">
            <a href="#" class="nav-link"><i class="nav-icon fas fa-camera icon-color"></i>
                <p>Device<i class="fas fa-angle-right right icon-color"></i> </p>
            </a>
            <ul class="nav nav-treeview listitem">
            </ul>

        <li class="nav-item">
          <a href="/gsiot/gsiot-listproduct" class="nav-link">
            <i class="nav-icon fas fa-search icon-color"></i>
            <p>
              Find Solution
            </p>
          </a>
        </li>


        <li class="nav-item">
          <a href="/gsiot/solution-management" class="nav-link">
            <i class="nav-icon fas fa-tasks icon-color"></i>
            <p>
              Solution Management
            </p>
          </a>
        </li>
        
      </ul>
    </nav>
    <!-- /.sidebar-menu -->
  </div>
  <!-- /.sidebar -->
</aside>
<script>
  loadSidebar();
  function loadSidebar() {
    $.ajax({
      type: "GET",
      url: "/gsiot/api/v1/get_data_menu_left",
      contentType: "application/json; charset=utf-8",
      success: function (rsp) {
        var status = rsp['status'];
        var total = rsp['total'];
        if (status == 1 && total > 0) {
          var data = rsp['data'];
          var htmlItemSidebar = "";
          for (var itemSidebar in data) {
            htmlItemSidebar += 
            '<li class="nav-item has-treeview">' +
              '<a href="#" class="nav-link">' +
              '<i class="nav-icon fas fa-camera icon-color"></i>' +
              '<p>' + data[itemSidebar][0].device_name + '<i class="fas fa-angle-right right icon-color"></i> </p>' +
              '</a>' +
              '<ul class="nav nav-treeview">';
            var htmlListItemSidebar = "";
            for (var i = 0; i < data[itemSidebar].length; i++) {
              htmlListItemSidebar += '<li class="nav-item">' +
                '<a href="'+data[itemSidebar][i].device_description+'?id_group='+data[itemSidebar][i]['group_id']+'" class="nav-link">' +
                '<i class="far fa-circle nav-icon icon-color"></i>' +
                '<p>' + data[itemSidebar][i].group_name + '</p>' +
                '</a>' +
                '</li>';
            }
            htmlItemSidebar += htmlListItemSidebar + '</ul></li>'
          }
          $(".listitem").prepend(htmlItemSidebar);
        }else{

        }
      },
      failure: function (error) {

      }

    });
  }
  
</script>