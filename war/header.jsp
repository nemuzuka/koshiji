<%-- 
/*
 * Copyright 2012 Kazumune Katagiri. (http://d.hatena.ne.jp/nemuzuka)
 * Licensed under the Apache License v2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */
 --%>
<%@page pageEncoding="UTF-8" isELIgnored="false"%>
<%@taglib prefix="f" uri="http://www.slim3.org/functions"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!-- Navbar
================================================== -->
<div class="navbar navbar-inverse navbar-fixed-top">
  <div class="navbar-inner">
    <div class="container">
      <button type="button" class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
      </button>
      <a class="brand" href="/">Koshiji</a>
      <div class="nav-collapse collapse">
        <ul class="nav">
          <li class="active dropdown">
          	<a id="memu1" href="#" role="button" class="dropdown-toggle" data-toggle="dropdown">
          	Message <b class="caret"></b>
          	</a>
          	<ul class="dropdown-menu" role="menu" aria-labelledby="memu1">
	            <li><a tabindex="-1" href="http://google.com">グループ管理</a></li>
	            <li class="divider"></li>
	            <li><a tabindex="-1" href="http://google.com">グループ削除</a></li>
	            <li><a tabindex="-1" href="http://google.com">グループ脱退</a></li>
	            <li class="divider"></li>
	            <li><a tabindex="-1" href="http://google.com">新規グループ作成</a></li>
            </ul>
          </li>
          <li class="">
            <a href="./getting-started.html">Schedule</a>
          </li>
          <li class="">
            <a href="./scaffolding.html">Settings</a>
          </li>
        </ul>
      </div>

    </div>
  </div>
</div>

