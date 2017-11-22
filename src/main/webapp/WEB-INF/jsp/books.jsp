<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="my" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>

<fmt:bundle basename="i18n">

    <fmt:message key="book.info.genre.fiction" var="book_info_genre_fiction"/>
    <fmt:message key="book.info.genre.literature.for.children" var="book_info_genre_literature_for_children"/>
    <fmt:message key="book.info.genre.poems" var="book_info_genre_poems"/>
    <fmt:message key="book.info.genre.history" var="book_info_genre_history"/>
    <fmt:message key="book.info.genre.society" var="book_info_genre_society"/>
    <fmt:message key="book.info.genre.ethnography" var="book_info_genre_ethnography"/>
    <fmt:message key="book.info.genre.politics" var="book_info_genre_politics"/>
    <fmt:message key="book.info.genre.economy" var="book_info_genre_economy"/>
    <fmt:message key="book.info.genre.culture" var="book_info_genre_culture"/>
    <fmt:message key="book.info.genre.biography" var="book_info_genre_biography"/>
    <fmt:message key="book.info.genre" var="book_info_genre"/>
    <fmt:message key="book.info.search" var="book_info_search"/>
    <fmt:message key="book.info.read.more" var="book_info_read_more"/>
    <fmt:message key="book.info.pholder.search" var="book_info_ph_search"/>
</fmt:bundle>

<my:designPattern role="${sessionScope.role}">

    <div class="container">
        <div class="row">
            <div class="col-md-4">
                <div class="panel panel-default">
                    <div class="panel-heading">
                        <h3 class="panel-title">${book_info_genre}</h3>
                    </div>

                    <ul class="list-group">
                        <c:forEach items="${genres}" var="genre">
                            <c:choose>
                                <c:when test="${genre.id == 1}">
                                    <a href="books?genre_id=1" class="list-group-item">${book_info_genre_fiction}</a>
                                </c:when>
                                <c:when test="${genre.id == 2}">
                                    <a href="books?genre_id=2" class="list-group-item">${book_info_genre_literature_for_children}</a>
                                </c:when>
                                <c:when test="${genre.id == 3}">
                                    <a href="books?genre_id=3" class="list-group-item">${book_info_genre_poems}</a>
                                </c:when>
                                <c:when test="${genre.id == 4}">
                                    <a href="books?genre_id=4" class="list-group-item">${book_info_genre_history}</a>
                                </c:when>
                                <c:when test="${genre.id == 5}">
                                    <a href="books?genre_id=5" class="list-group-item">${book_info_genre_society}</a>
                                </c:when>
                                <c:when test="${genre.id == 6}">
                                    <a href="books?genre_id=6" class="list-group-item">${book_info_genre_ethnography}</a>
                                </c:when>
                                <c:when test="${genre.id == 7}">
                                    <a href="books?genre_id=7" class="list-group-item">${book_info_genre_politics}</a>
                                </c:when>
                                <c:when test="${genre.id == 8}">
                                    <a href="books?genre_id=8" class="list-group-item">${book_info_genre_economy}</a>
                                </c:when>
                                <c:when test="${genre.id == 9}">
                                    <a href="books?genre_id=9" class="list-group-item">${book_info_genre_culture}</a>
                                </c:when>
                                <c:when test="${genre.id == 10}">
                                    <a href="books?genre_id=10" class="list-group-item">${book_info_genre_biography}</a>
                                </c:when>
                            </c:choose>
                        </c:forEach>
                    </ul>
                </div>
            </div>

            <div class="col-md-8">
                <form role="form">
                    <div class="row">
                        <div class="form-group">
                            <div class="input-group">
                                <input class="form-control" type="text" name="search"
                                       placeholder=${book_info_ph_search}/>
                                <span class="input-group-btn">
                            <button class="btn btn-success" type="submit"><span
                                    style="margin-left:10px;">${book_info_search}</span></button>
                        </span>
                                </span>
                            </div>
                        </div>
                    </div>


                    <c:forEach items="${books}" var="book">
                    <hr>
                    <h1><font color="#5f9ea0"><c:out value="${book.name}"/></font></h1>
                    <p><c:out value="${book.description}"/></p>
                    <div>
                        <div class="more label"><a href="aboutBook?book_id=${book.id}">${book_info_read_more}</a></div>
                    </div>
                    <hr>
                    </c:forEach>
            </div>
        </div>
        <div class="row">
            <div class="col-md-offset-5">
                <my:listPages currentPage="${currentPage}" noOfPages="${noOfPages}" books_url="books?page="/>
            </div>
        </div>
    </div>


</my:designPattern>


