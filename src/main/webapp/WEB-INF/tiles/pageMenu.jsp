<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="${sessionBean.language}" />
<fmt:setBundle basename="global" />

        <div class="col-md-3 left_col">
          <div class="left_col scroll-view">
            <div class="navbar nav_title" style="border: 0;">
              <a href="index.html" class="site_title"><i class="fa fa-paw"></i> <span><fmt:message key="application.title" /></span></a>
            </div>

            <div class="clearfix"></div>

            <!-- menu profile quick info -->
            <div class="profile clearfix">
              <div class="profile_pic">
                <img src="images/img.jpg" alt="..." class="img-circle profile_img">
              </div>
              <div class="profile_info">
                <span><fmt:message key="menu.welcome" />,</span>
                <h2>${sessionBean.user.personName} ${sessionBean.user.personSurname}</h2>
              </div>
            </div>
            <!-- /menu profile quick info -->

            <br />

            <!-- sidebar menu -->
            <%-- 
            
            Gentelella supports a 3-levels menu, where 1st level is used for "menu sections". 
            
            --%>
            <div id="sidebar-menu" class="main_menu_side hidden-print main_menu">
              <c:forEach items="${sessionBean.menus}" var="section">
              <div class="menu_section">
                <h3><fmt:message key="${section.description}"/></h3>
                <ul class="nav side-menu">

                <c:forEach items="${section.submenus}" var="m">
                  <li><a><i class="fa fa-${m.icon}"></i> <fmt:message key="${m.description}"/> <span class="fa fa-chevron-down"></span></a>
                    <ul class="nav child_menu">
		              <c:forEach items="${m.submenus}" var="s">
                        <li><a><fmt:message key="${s.description}"/><span class="fa fa-chevron-down"></span></a>
                          <ul class="nav child_menu">
	                      <c:forEach items="${s.pages}" var="p2">
                            <li class="sub_menu"><a href="${p2.url}"><fmt:message key="${p2.description}"/></a>
                            </li>
	                      </c:forEach>
	                      </ul>
                      </c:forEach>
		              <c:forEach items="${m.pages}" var="p">
                      <li><a href="${p.url}">
                      		<fmt:message key="${p.description}"/>
							</a></li>
                      </c:forEach>
                    </ul>
                  </li>
                </c:forEach>
                
                </ul>
              </div>
			  </c:forEach>
			  
            </div>
            <!-- /sidebar menu -->

            <!-- /menu footer buttons -->
            <fmt:message key="menu.settings" var="settings" />
            <fmt:message key="menu.fullscreen" var="fullscreen" />
            <fmt:message key="menu.lock" var="lock" />
            <fmt:message key="menu.logout" var="logout" />
            
            <div class="sidebar-footer hidden-small">
              <a data-toggle="tooltip" data-placement="top" title="${settings}">
                <span class="glyphicon glyphicon-cog" aria-hidden="true"></span>
              </a>
              <a data-toggle="tooltip" data-placement="top" title="${fullscreen}">
                <span class="glyphicon glyphicon-fullscreen" aria-hidden="true"></span>
              </a>
              <a data-toggle="tooltip" data-placement="top" title="${lock}">
                <span class="glyphicon glyphicon-eye-close" aria-hidden="true"></span>
              </a>
              <a data-toggle="tooltip" data-placement="top" title="${logout}" href="doLogout">
                <span class="glyphicon glyphicon-off" aria-hidden="true"></span>
              </a>
            </div>
            <!-- /menu footer buttons -->
          </div>
        </div>
