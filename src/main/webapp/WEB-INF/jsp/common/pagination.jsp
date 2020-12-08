<c:if test="${pagination.totalPage > 1}">
	<!-- Pagination -->
	<div class="datatable-footer">
		<div class="dataTables_paginate paging_simple_numbers" id="pagination">
			<a class="paginate_button previous <c:if test="pagination.isFirst()}">disabled</c:if>" href="${pagination.getUrl(pagination.currentPage-1)}" id="paginaton_btn_previous">
				<i class="fa fa-long-arrow-left"></i>
			</a>
			<span>
				<c:forEach items="${pagination.pages}" var="page">
					<c:if test="${pagination.currentPage == page}">
						<a class="paginate_button current" href="javascript:void(0)">${page}</a>
					</c:if>
					<c:if test="${pagination.currentPage != page}">
						<a class="paginate_button " href="${pagination.getUrl(page)}">${page}</a>
					</c:if>
				</c:forEach>
			</span>
			<a class="paginate_button next <c:if test="${pagination.isLast()}">disabled</c:if>" href="${pagination.getUrl(pagination.currentPage+1)}" id="paginaton_btn_next">
				<i class="fa fa-long-arrow-right"></i>
			</a>
		</div>
	</div>
	<!-- /pagination -->
	</c:if>
