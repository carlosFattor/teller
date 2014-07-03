/*
 * Happy Melly Teller
 * Copyright (C) 2013 - 2014, Happy Melly http://www.happymelly.com
 *
 * This file is part of the Happy Melly Teller.
 *
 * Happy Melly Teller is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Happy Melly Teller is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Happy Melly Teller.  If not, see <http://www.gnu.org/licenses/>.
 *
 * If you have questions concerning this license or the applicable additional terms, you may contact
 * by email Sergey Kotlov, sergey.kotlov@happymelly.com or
 * in writing Happy Melly One, Handelsplein 37, Rotterdam, The Netherlands, 3071 PR
 */

/**
 * Filter evaluations by an event
 */
function filterByEvent(oSettings, aData, iDataIndex) {
    var index = 9;
    var filter = $('#events').find(':selected').val();
    if (filter == '') {
        return true;
    }
    return aData[index] == filter;
}

/**
 * Leave the events, facilitated by the user
 */
function filterByFacilitator(oSettings, aData, iDataIndex) {
    var index = 10;
    var state = $('#facilitatedByMe').is(':checked');
    if (state == false) {
        return true;
    }
    return aData[index] == 1;
}

/**
 * Filter evaluations checking if they are pending, approved or rejected
 */
function filterByStatus(oSettings, aData, iDataIndex) {
    var index = 0;
    var filter = $('#status').find(':selected').val();
    if (filter == 'all') {
        return true;
    }
    var value = $(aData[index]).attr('value');
    return value == filter;
}

$.fn.dataTableExt.afnFiltering.push(filterByStatus);
$.fn.dataTableExt.afnFiltering.push(filterByEvent);
$.fn.dataTableExt.afnFiltering.push(filterByFacilitator);

function loadEventList(events) {
    $('#events').empty().append($("<option></option>").attr("value", "").text("Select an event"));
    for(var i = 0; i < events.length; i++) {
        var event = events[i];
        $('#events').append( $('<option value="'+ event.id +'">' + event.longTitle +'</option>') );
    }
}


$(document).ready( function() {
    var currentBrand = $('#brands').val();
    var brandInSession = $('#participants').attr('brandCode');
    if (brandInSession) {
        currentBrand = brandInSession;
        $("#brands option[value='" + currentBrand + "']").attr('selected', 'selected');
    }
    var events = [];
    var participantTable = $('#participants').dataTable({
        "sDom": '<"toolbar">rtip',
        "iDisplayLength": 25,
        "asStripeClasses":[],
        "aaSorting": [],
        "bLengthChange": false,
        "ajax": {
            "url" : "participants/brand/" + currentBrand,
            "dataSrc": ""
        },
        "order": [[ 6, "desc" ]],
        "columns": [
            { "data": "evaluation.status" },
            { "data": "person" },
            { "data": "event" },
            { "data": "location" },
            { "data": "schedule" },
            { "data": "evaluation.impression" },
            { "data": "evaluation.creation" },
            { "data": "evaluation.handled" },
            { "data": "evaluation.certificate" },
            { "data": "event" },
            { "data": "event" },
            { "data": "actions" }
        ],
        "columnDefs": [{
                "render": function(data, type, row) { return drawStatus(data); },
                "targets": 0
            }, {
                "render": function(data, type, row) {
                    return '<a href="' + data.url + '">' + data.name + '</a>';
                },
                "targets": 1
            }, {
                "render": function(data, type, row) {
                    var result = $.grep(events, function(e){ return e.url == data.url; });
                    if (result.length == 0) {
                        events.push(data);
                    }
                    return '<a href="' + data.url + '">' + data.title + '</a>';
                },
                "targets": 2
            }, {
                "render": function(data, type, row) {
                    return '<img align="absmiddle" width="16" src="/assets/images/flags/16/' +
                        data.country + '.png"/> ' + data.city;
                },
                "targets": 3
            }, {
                "render": function(data, type, row) {
                    return data.start + ' / ' + data.end;
                },
                "targets": 4
            }, {
                "render": function(data, type, row) { return drawImpression(data); },
                "targets": 5
            }, {
                "render": function(data, type, row) { return drawCertificate(data); },
                "targets": 8
            }, {
                "render": function(data, type, row) { return data.id; },
                "visible": false,
                "targets": 9
            },{
                "render": function(data, type, row) {
                    return data.facilitatedByMe ? 1 : 0;
                },
                "targets": 10,
                "visible": false
            }, {
               "render": function(data, type, row) { return renderDropdown(data); },
               "targets": 11,
               "bSortable": false
            }
        ]
    });
    participantTable
        .api()
        .on('init.dt', function (e, settings, data) {
            loadEventList(events);
        });

    $("div.toolbar").html($('#filter-containter').html());
    $('#filter-containter').empty();
    $('#status').on('change', function() {
        participantTable.fnDraw();
    });
    $("#events").on('change', function() {
        participantTable.fnDraw();
    });
    $("#facilitatedByMe").on('change', function() {
        participantTable.fnDraw();
    });

    $("#brands").change(function() {
        var brandCode = $(this).find(':selected').val();
        events = [];
        participantTable
            .api()
            .ajax
            .url("participants/brand/" + brandCode)
            .load(function(){
                loadEventList(events);
            });
    });
    $('#participants').on('draw.dt', function() {
        calculateAverageImpression(participantTable);
    });
});
