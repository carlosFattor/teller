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

function User(data) {
    this.name = data["first_name"] + " " + data["last_name"];
    this.id = data["id"];
    this.memberships = data["memberships"];
}

User.prototype.isFacilitator = function(userId) {
    return this.id == userId;
};

function showError(message) {
    $('#error').append(
        $('<div class="alert alert-danger">')
            .text(message)
            .append('<button type="button" class="close" data-dismiss="alert">&times;</button>')
    );
}

/**
 * Retrieve a list of event types for the brand
 * @param brandId {int}
 * @param currentEventType String
 */
function getEventTypes(brandId, currentEventType) {
    $.ajax({
        url: '/brand/' + brandId + '/eventtypes',
        dataType: "json"
    }).done(function(data) {
            var selector = "#eventTypeId";
            var value = parseInt($(selector).attr('value'));
            $(selector)
                .empty()
                .append($("<option></option>").attr("value", 0).text("Choose an event type"));
            for(var i = 0; i < data.length; i++) {
                var option = $("<option></option>")
                    .attr("value", data[i].id)
                    .attr("defaultTitle", data[i].title)
                    .attr("free", data[i].free)
                    .text(data[i].name);
                if (value == data[i].id) {
                    option.attr('selected', 'selected');
                }
                $(selector).append(option);
            }
            if (currentEventType) {
                selector = 'option[value="' + currentEventType + '"]';
                var object = $('#eventTypeId').find(selector)
                object.attr('selected', 'selected');
                toggleFreeCheckbox(object.attr('free'));
            }
        }).fail(function() {
            showError("Sorry we don't know anything about the brand you try to request");
        });
}

/**
 * Update 'Title' field
 * @param title String
 */
function updateTitle(title) {
    $("#title").val(title);
}

/**
 * Update a list of organisations for invoicing
 * @param organisations Array
 * @param selectedId Int
 */
function updateInvoicingOrganisations(organisations, selectedId) {
    $('#invoice_invoiceTo')
        .empty()
        .append($("<option></option>").attr("value", 0).text("Choose an organisation"));
    if (organisations.length == 0) {
        var message = "Warning: You are not able to save any event information because there is no organization ";
        message += "connected to the accounts of the chosen facilitators. Please ask the person who added ";
        message += "your account also connect it to an organization that will be used for invoicing.";
        $('#no_org_warning').empty().append(
            $('<div class="alert alert-danger">')
                .text(message)
        );
    } else {
        $('#no_org_warning').empty();
    }
    for(var key in organisations) {
        var option = $("<option></option>").attr("value", key).text(organisations[key]);
        if (key == selectedId) {
            option.attr('selected', 'selected');
        }
        $('#invoice_invoiceTo').append(option);
    }
}

/**
 * Calculates number of total hours for event based on its number of days and
 * hours per day
 */
function calculateTotalHours() {
    var end = $('#schedule_end').data("DateTimePicker").getDate();
    var start = $('#schedule_start').data('DateTimePicker').getDate();
    var days = end.diff(start, 'days') + 1;
    return $('#schedule_hoursPerDay').val() * days;
}
/**
 * Updates number of total hours for event based on its number
 * of days and hours per day
 */
function updateTotalHours() {
    var totalHours = calculateTotalHours();
    $('#schedule_totalHours').val(totalHours);
}

/**
 * Checks if the entered number of total hours is move than minimum threshold
 * and shows an alert
 * @param hours Number of entered total hours
 */
function checkTotalHours(hours) {
    var idealHours = calculateTotalHours();
    var threshold = 0.2;
    var difference = (idealHours - hours) / parseFloat(idealHours);
    if (difference > threshold) {
        $('#totalHours-alert').show();
    } else {
        $('#totalHours-alert').hide();
    }
}

/**
 * Checks if the given url points to an existing page and notifies a user about
 *  the results of the check
 *
 * @param url {string} The url of interest
 * @param element {string} jQuery selector
 */
function checkUrl(url, element) {
    var field = element + '_field';
    if ($.trim(url).length == 0 || (url.substring(0, 6) == "mailto")) {
        $(field).removeClass('has-error');
        $(field).removeClass('has-success');
        $(element).siblings('span').each(function() {
            $(this).text("Web site URL");
        });
    } else {
        var fullUrl = jsRoutes.controllers.Urls.validate(url).url;
        $.post(fullUrl, {}, null, "json").done(function(data) {
            if (data.result == "invalid") {
                $(field).addClass('has-error');
                $(element).siblings('span').each(function() {
                    $(this).text("URL is not correct");
                });
            } else {
                $(field).removeClass('has-error');
                $(field).addClass('has-success');
                $(element).siblings('span').each(function() {
                    $(this).text("URL is correct");
                });
            }
        });
    }
}

/**
 * Shows/hides free checkbox depending on if event type allows free events
 *
 * @param value True if free events are allowed
 */
function toggleFreeCheckbox(value) {
    if (value == "true") {
        $("#free_field").show();
    } else {
        $("#free_field").hide();
    }
}

$(document).ready( function() {

    /**
     * Check if we should add a user to the list of chosen facilitators or not
     * @param user User
     * @param chosenFacilitators Array[Int]
     * @returns Boolean
     */
    function isChosenOne(user, chosenFacilitators) {
        if (user.isFacilitator(facilitators.userId)) return true;
        return (chosenFacilitators && chosenFacilitators.indexOf(user.id.toString()) >= 0);
    }

    /**
     * Retrieve a list of facilitators for the brand and fill 'chosen' and 'retrieved' arrays
     * in 'facilitators' object
     * @param brandId String
     * @param chosenFacilitators Array[Int] or null
     */
    function getFacilitators(brandId, chosenFacilitators) {
        $.ajax({
            url: '/brand/' + brandId + '/facilitators',
            dataType: "json"
        }).done(function(data) {
            for(var i = 0; i < data.length; i++) {
                var user = new User(data[i]);
                if (isChosenOne(user, chosenFacilitators)) {
                    facilitators.chosen[facilitators.chosen.length] = user;
                } else {
                    facilitators.retrieved[facilitators.retrieved.length] = user;
                }
            }
            facilitators.updateState();
        }).fail(function() {
            showError("Sorry we don't know anything about the brand you try to request");
        });
    }

    var facilitators = {
        retrieved: [],
        chosen: [],
        userId: 0,
        invoiceOrgId: 0,
        initialize: function(brandId) {
            this.userId = parseInt($('#currentUserId').attr('value'));
            this.invoiceOrgId = parseInt($('#currentInvoiceToId').attr('value'));
            var values = $('#chosenFacilitators').attr('value').split(',');
            getFacilitators(brandId, values);
        },
        retrieve: function(brandId) {
            this.retrieved = [];
            this.chosen = [];
            getFacilitators(brandId, null);
        },
        updateState: function() {
            // update a list of available facilitators
            $('#facilitatorIds')
                .empty()
                .append($("<option></option>").attr("value", 0).text(""));
            this.retrieved.sort(this.sortByName);
            for(var i = 0; i < this.retrieved.length; i++) {
                var name = this.retrieved[i].name;
                $('#facilitatorIds').append($("<option></option>").attr("value", this.retrieved[i].id).text(name));
            }
            // update a list of chosen facilitators
            $('#chosenFacilitators').empty();
            this.chosen.sort(this.sortByName);
            var organisations = [];
            for(var i = 0; i < this.chosen.length; i++) {
                var user = this.chosen[i];
                for(var j = 0; j < user.memberships.length; j++) {
                    var company = user.memberships[j];
                    if (organisations.indexOf(company.id) > 0) {
                        continue;
                    } else {
                        organisations[company.id] = company.name;
                    }
                }
                var parentDiv = $("<div>").append(
                    $("<input readonly type='hidden'>")
                        .attr("value", user.id)
                        .attr("id", "facilitatorIds_" + i)
                        .attr('name', 'facilitatorIds[' + i + ']')
                );
                var div = $("<div class='input-group'>").append(
                    $("<input readonly type='text' class='form-control'>")
                        .attr("value", user.name)
                );
                parentDiv.append(div);
                var trashCan = $('<i>')
                    .attr('class', 'glyphicon glyphicon-trash');
                var button = $('<button>').attr('type', 'button');
                if (!user.isFacilitator(this.userId)) {
                    button.attr('class', 'btn btn-danger deselect');
                } else {
                    button
                        .attr('class', 'btn btn-danger')
                        .attr('disabled', 'disabled');
                }
                button.append(trashCan);
                var span = $('<span class="input-group-btn">').append(button);
                div.append(span);
                $('#chosenFacilitators').append(parentDiv);
            }
            updateInvoicingOrganisations(organisations, this.invoiceOrgId);
        },
        select: function(id) {
            this.move(id, this.retrieved, this.chosen);
        },
        deselect: function(id) {
            this.move(id, this.chosen, this.retrieved);
        },
        move: function(id, from, to) {
            var index = -1;
            for(var i = 0; i < from.length; i++) {
                if (from[i].id == id) {
                    index = i;
                    break;
                }
            }
            if (index != -1) {
                to[to.length] = from[index];
                from.splice(index, 1);
            }
            this.updateState();
        },
        sortByName: function(left, right) {
            return left.name.localeCompare(right.name);
        }
    };

    // Binds
    $("#brandId").change(function() {
        var id = $(this).find(':selected').val();
        getEventTypes(id, "");
        facilitators.retrieve(id);
    });
    $('#facilitatorIds').change(function() {
        facilitators.select($(this).find(':selected').val());
    });
    $(this).on('click', '.deselect', function(event) {
        event.preventDefault();
        var id = $(this).parent().parent().parent('div').children('input').first().val();
        facilitators.deselect(id);
    });
    $("#schedule_start").on("dp.change", function (e) {
        $('#schedule_end').data("DateTimePicker").setMinDate(e.date);
        updateTotalHours();
    });
    $("#schedule_end").on("dp.change", function(e) {
        updateTotalHours();
    });
    $('#schedule_totalHours').on('change', function(e) {
        checkTotalHours($(this).val());
    });
    $('#details_webSite').on('change', function(e) {
        checkUrl($(this).val(), '#details_webSite');
    });
    $('#details_registrationPage').on('change', function(e) {
        checkUrl($(this).val(), '#details_registrationPage');
    });
    var brandId = $('#brandId').find(':selected').val();
    getEventTypes(brandId, $('#currentEventTypeId').attr('value'));
    facilitators.initialize(brandId);
    if ($("#emptyForm").attr("value") == 'true') {
        $("#schedule_start").on("dp.change", function (e) {
            $('#schedule_end').data("DateTimePicker").setDate(e.date.add(1, 'days'))
        });
        $("#eventTypeId").change(function() {
            var option = $(this).find(':selected');
            if (option.attr('defaultTitle')) {
                updateTitle(option.attr('defaultTitle'));
            }
        });
        $("#title").on('keyup', function() {
            $("#eventTypeId").unbind('change');
        });
        updateTotalHours(8);
        $("#free_field").hide();
    }
    $("#eventTypeId").change(function(event) {
        var option = $(this).find(':selected');
        toggleFreeCheckbox(option.attr('free'));
    });
    checkTotalHours($('#schedule_totalHours').val());

    if ($("#confirmed").attr("checked") != "checked") {
        $("#confirmed-alert").hide();
    }
    $("#confirmed").on("change", function(e) {
        if (this.checked) {
            $("#confirmed-alert").show();
        } else {
            $("#confirmed-alert").hide();
        }
    });
});

