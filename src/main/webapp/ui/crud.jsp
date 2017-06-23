<!DOCTYPE html>
<%@ page contentType="text/html"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="${sessionBean.language}" />
<fmt:setBundle basename="com.example.myapp.crud.crud" />
<c:set var="entity" value="${ param.entity }" />

<t:template>

          <div class="">
            <div class="page-title">
              <div class="title_left">
                <h3><fmt:message key="title.${entity}" /></h3>
              </div>

              <div class="title_right">
                <div class="col-md-5 col-sm-5 col-xs-12 form-group pull-right top_search">
                  <div class="input-group">
                    <input type="text" class="form-control" placeholder="Search for...">
                    <span class="input-group-btn">
                      <button class="btn btn-default" type="button">Go!</button>
                    </span>
                  </div>
                </div>
              </div>
            </div>

            <div class="clearfix"></div>

            <div class="row">
              <div class="col-md-12 col-sm-12 col-xs-12">
                <div class="x_panel">
                  <div class="x_title">
                    <h2><fmt:message key="title.${entity}" /></h2>
                    <ul class="nav navbar-right panel_toolbox">
                      <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
                      </li>
                    </ul>
                    <div class="clearfix"></div>
                  </div>
                  <div class="x_content">
                      <table id="mainTable"  class="table table-striped table-bordered dt-responsive nowrap" >
                      </table>
                  </div>
                </div>
              </div>
            </div>
          </div>
    
    <script>
     var entity = '${entity}';
    </script>      
	<script src="../vendors/datatables.net/js/jquery.dataTables.min.js"></script>
	<script src="../vendors/bootstrap-growl/js/bootstrap-growl.min.js"></script>
	<script src="js/crud/crud.js"></script>
	
</t:template>