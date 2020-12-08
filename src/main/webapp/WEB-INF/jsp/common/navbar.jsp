<style>
  .navbara {
    display: flex;
    padding-bottom: 5px !important;
  }
</style>
<!-- Navbar -->
<nav class="main-header navbara navbar-expand navbar-white navbar-light" style="background: #ffffff;">
  <!-- Left navbar links -->
  <ul class="navbar-nav" style="margin-bottom: 10px;">
    <li class="nav-item">
      <a class="nav-link" data-widget="pushmenu" href="#"><i class="fas fa-bars"></i></a>
    </li>
  </ul>

  <!-- Right navbar links -->
  <ul class="navbar-nav ml-auto">
    <li class="nav-item dropdown dropdown-hover">
      <a class="nav-link" data-toggle="dropdown" href="#">
        <i class="far fa-user"></i><span class="caret"></span>
      </a>
      <div class="dropdown-menu dropdown-menu-sm dropdown-menu-right">
        <!-- <div class="dropdown-divider"></div> -->
        <a href="#" class="dropdown-item" style="font-size: 14px;">
          <i class="fas fa-user" style="margin-right: 5px;"></i>Profile
        </a>
        <div class="dropdown-divider"></div>
        <a href="#" onclick="logout()" class="dropdown-item" style="font-size: 14px;">
          <i class="fas fa-sign-out-alt" style="margin-right: 5px;"></i>Logout
        </a>
      </div>
    </li>
  </ul>
</nav>
<!-- /.navbar -->