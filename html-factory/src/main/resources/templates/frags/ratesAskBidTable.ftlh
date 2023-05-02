<table>
<tr><th>Currency:</th><th>Ask:</th><th>Bid:</th><th>Trading date:</th></tr>
<#list report.ratesByDate as headers>
<tr><th colspan="3">Published at: ${headers.published} in ${headers.nbpTable}</th></tr>
<#list headers.rates as exchRate> 
<tr><td>${exchRate.currency.code}</td><td>${exchRate.ask}</td><td>${exchRate.bid}</td><td>${exchRate.tradingDate}</td></tr>
</#list>
</#list>
</table>