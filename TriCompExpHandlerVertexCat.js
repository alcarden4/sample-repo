/**
 * Created by Anna Cardenas on 6/16/16.
 * Code based from Yuval Hart.
 */

Global_info = {};
Global_info.start = Date.now();
Global_info.end = 0;
Global_info.round = 0; //0 = before change; 1 = after change
Global_info.history = [];
Global_info.sideRunNum = 0; //indicates which Triangle configuration side length is up next
Global_info.angleRunNum = 0; //indicates which Triangle configuration angle size is up next
Global_info.consent = 0;
Global_info.comments = 0;
Global_info.TotRuns = 120;

/** Variable that decides which question to ask next (chooses randomly)  */
Global_info.QuestionNum = 0;

// Global_info.curPage=0;

/** Used to record subject responses */
Global_info.userResponse = '';
/** Records base length, angle, and question type */
Global_info.setup = '';
/** Gives user Id  */
Global_info.userID = '';

/** Global variables to check if each button has been pushed. */
Global_info.IncDist = 0;
Global_info.DecDist = 0;
Global_info.IncAng = 0;
Global_info.DecAng = 0;


/** Global functions for triangle training. (probably not the best way) */
var IncrDist;
var DecrDist;
var IncrAng;
var DecrAng;
var OrigTri;


var increase = "increase ".bold();
var decrease = "decrease ".bold();

var distance = "distance ".bold();

var bigger_smaller = " get bigger, get smaller, or stay the same size?".italics();
var upward_downward = " move up, move down, or stay in the same place?".italics();
var angleSize = "angle size ".bold();
var topCorner = "top corner ".bold();
var Location = "location ".bold(); // added

var Questions = [
"Imagine we " + increase + "the " + angleSize + "of the bottom two corners by 20%. Will the " + angleSize + "of"
+ " the " + topCorner + bigger_smaller,

"Imagine we " + decrease + "the " + angleSize + "of the bottom two corners by 20%. Will the " + angleSize + "of"
+ " the " + topCorner + bigger_smaller,

"Imagine we " + increase + "the " + distance + "between the bottom two corners by 20%. Will the "
+ angleSize + "of the " + topCorner + bigger_smaller,

"Imagine we " + decrease + "the " + distance + "between the bottom two corners by 20%. Will the " + angleSize
+ "of the " + topCorner + bigger_smaller,

"Imagine we " + increase + "the " + distance + "between the bottom two corners by 20%. Will the " + Location + "of" +
" the " + topCorner + upward_downward,

"Imagine we " + decrease + "the " + distance +
"between the bottom two corners by 20%. Will the " + Location + "of the " + topCorner + upward_downward,

"Imagine we " + increase + "the "  + angleSize + "of the bottom two corners by 20%, will the " + Location + "of" +
" the " + topCorner + upward_downward,

"Imagine we " + decrease + "the " + angleSize + "of the bottom two corners by 20%. Will the " + Location + "of" +
" the " + topCorner + upward_downward];

var Answers = ["smaller", "bigger", "same", "same", "upward", "downward", "upward", "downward"];

//Create a Random array of runs for this subject, runs #'s go from 1-10
RunNumOrder = getRandomArray(_.range(0, Global_info.TotRuns), Global_info.TotRuns);

// A function that shuffles the array
function getRandomArray(arr, size) {
    var shuffled = arr.slice(0), i = arr.length, temp, index;
    while (i--) {
        index = Math.floor((i + 1) * Math.random());
        temp = shuffled[index];
        shuffled[index] = shuffled[i];
        shuffled[i] = temp;
    }
    return shuffled.slice(0, size);
};


// Function that handles the first training triangle (teach subjects what we mean by extending distance and angles)
function trainTriangle() {

    $('#triangleTrain').empty();
    var paper2 = Snap("#triangleTrain").attr({width: "1500", height: "1000"});


    // drawing the triangle
    //parameters of the run:
    var LengthAngleSideOrig = 100;
    var AngleOrig = Math.PI / 4; //Size of angle in radians - 45 deg
    var LengthBaseOrig = 600; //Max Base Length
    var TriBaseXStartOrig = 150; //Origin position in the X axis for maximal base length
    var TriBaseXEndOrig = LengthBaseOrig + TriBaseXStartOrig; //End position of base for maximal base length
    var BaseLengthFactor = 0.7; //Get the current percent of side length from Global_info.sideRunNum
    var BaseLength = LengthBaseOrig * BaseLengthFactor;
    var TriBaseXStart = TriBaseXStartOrig + 0.5 * (1 - BaseLengthFactor) * LengthBaseOrig;
    var TriBaseXEnd = TriBaseXStart + BaseLength;
    var TriBaseYPos = 100;
    var TriSideXLengthIn = LengthAngleSideOrig * BaseLengthFactor;
    var TriSideYLengthUp = Math.tan(AngleOrig) * LengthAngleSideOrig * BaseLengthFactor;


    //drawing the triangle
    function DrawTestTriangle(tri_base_x_start, tri_base_x_end, tri_base_y_pos, tri_side_x_len, tri_side_y_len) {
        var triBaseLeft = paper2.line(tri_base_x_start, tri_base_y_pos, tri_base_x_start +
            tri_side_x_len * 2, tri_base_y_pos).attr({strokeWidth: 5, stroke: "black", strokeLinecap: "round"});
        var triBaseRight = paper2.line(tri_base_x_end, tri_base_y_pos, tri_base_x_end -
            tri_side_x_len * 2, tri_base_y_pos).attr({strokeWidth: 5, stroke: "black", strokeLinecap: "round"});
        var triRightSide = paper2.line(tri_base_x_end, tri_base_y_pos, tri_base_x_end -
            tri_side_x_len, tri_base_y_pos - tri_side_y_len).attr({
            strokeWidth: 5,
            stroke: "black",
            strokeLinecap: "round"
        });
        var triLeftSide = paper2.line(tri_base_x_start, tri_base_y_pos, tri_base_x_start + tri_side_x_len,
            tri_base_y_pos - tri_side_y_len).attr({strokeWidth: 5, stroke: "black", strokeLinecap: "round"});
    };
    DrawTestTriangle(TriBaseXStart, TriBaseXEnd, TriBaseYPos, TriSideXLengthIn, TriSideYLengthUp);


    /** Functions for manipulating the training triangle. */


    IncrDist = function () {
        paper2.clear();
        DrawTestTriangle(TriBaseXStart - 100, TriBaseXEnd + 100, TriBaseYPos, TriSideXLengthIn, TriSideYLengthUp);

    };

    DecrDist = function () {
        paper2.clear();
        DrawTestTriangle(TriBaseXStart, TriBaseXEnd - 50, TriBaseYPos, TriSideXLengthIn, TriSideYLengthUp);
    };

    IncrAng = function () {
        paper2.clear();
        TriSideYLengthUp = Math.tan(Math.PI / 3) * LengthAngleSideOrig * BaseLengthFactor;
        DrawTestTriangle(TriBaseXStart, TriBaseXEnd, TriBaseYPos, TriSideXLengthIn, TriSideYLengthUp);
        TriSideYLengthUp = Math.tan(AngleOrig) * LengthAngleSideOrig * BaseLengthFactor;

    };

    DecrAng = function () {
        paper2.clear();
        TriSideYLengthUp = Math.tan(Math.PI / 6) * LengthAngleSideOrig * BaseLengthFactor;
        DrawTestTriangle(TriBaseXStart, TriBaseXEnd, TriBaseYPos, TriSideXLengthIn, TriSideYLengthUp);
        TriSideYLengthUp = Math.tan(AngleOrig) * LengthAngleSideOrig * BaseLengthFactor;
    };

    OrigTri = function () {
        paper2.clear();
        DrawTestTriangle(TriBaseXStart, TriBaseXEnd, TriBaseYPos, TriSideXLengthIn, TriSideYLengthUp);
    }


};

/** The function that draws the experiment triangles */
function drawTriangle() {
    //alert('drawTriangle'+Global_info.curPage)
    $('#triangle').empty();



    var paper = Snap("#triangle").attr({width: "1500", height: "1000"});

    var TriBaseLengthPerArray = [1, 0.75, 0.5, 0.25, 0.1];
    var TriBaseAngleArray = [Math.PI / 6, Math.PI / 4, Math.PI / 3];
    var dist = function (pt1, pt2) {
        var dx = pt1.x - pt2.x;
        var dy = pt1.y - pt2.y;
        return Math.sqrt(dx * dx + dy * dy);
    };

    // drawing the triangle
    //parameters of the run:
    var LengthAngleSideOrig = 100;
    var AngleOrig = TriBaseAngleArray[Math.floor(Global_info.angleRunNum % 3)]; //Size of angle in radians
    /** Gets question from array of questions. */
    Global_info.QuestionNum = Global_info.angleRunNum;
    /** Question is a string of the actual question */
    var Question = Questions[Math.floor(Global_info.QuestionNum % 8)];
    /** Answer to the question used for logging */
    var Answer = Answers[Math.floor(Global_info.QuestionNum % 8)];

    var LengthBaseOrig = 600; //Max Base Length
    var TriBaseXStartOrig = 150; //Origin position in the X axis for maximal base length
    var TriBaseXEndOrig = LengthBaseOrig + TriBaseXStartOrig; //End position of base for maximal base length
    var BaseLengthFactor = TriBaseLengthPerArray[Math.floor(Global_info.sideRunNum % 5)];
    // Get the current percent of side length from Global_info.sideRunNum
    var BaseLength = LengthBaseOrig * BaseLengthFactor;
    var TriBaseXStart = TriBaseXStartOrig + 0.5 * (1 - BaseLengthFactor) * LengthBaseOrig;
    var TriBaseXEnd = TriBaseXStart + BaseLength;
    var TriBaseYPos = LengthBaseOrig - 100;
    var TriSideXLengthIn = LengthAngleSideOrig * BaseLengthFactor;
    var TriSideYLengthUp = Math.tan(AngleOrig) * LengthAngleSideOrig * BaseLengthFactor;


    //drawing the triangle
    var triBaseLeft = paper.line(TriBaseXStart, TriBaseYPos, TriBaseXStart + TriSideXLengthIn * 2, TriBaseYPos).attr(
        {strokeWidth: 5, stroke: "black", strokeLinecap: "round"});
    var triBaseRight = paper.line(TriBaseXEnd, TriBaseYPos, TriBaseXEnd - TriSideXLengthIn * 2, TriBaseYPos).attr(
        {strokeWidth: 5, stroke: "black", strokeLinecap: "round"});
    var triRightSide = paper.line(TriBaseXEnd, TriBaseYPos, TriBaseXEnd - TriSideXLengthIn,
        TriBaseYPos - TriSideYLengthUp).attr({strokeWidth: 5, stroke: "black", strokeLinecap: "round"});
    var triLeftSide = paper.line(TriBaseXStart, TriBaseYPos, TriBaseXStart + TriSideXLengthIn, TriBaseYPos -
        TriSideYLengthUp).attr({strokeWidth: 5, stroke: "black", strokeLinecap: "round"});


    /** Displays a random question. */
    document.getElementById("question").innerHTML = Question;
    if (Question.includes("location")) {
        var QuestionType = 'vertex';
    }
    else {
        QuestionType = 'angle';
    }

    /** Getting what was being manipulated in the question: distance/angle */
    if (Question.includes("distance")) {
        var QuestionVariable = "distance";
    }
    else {
        QuestionVariable = "angle";
    }

    var ButtonX = TriBaseXEndOrig;
    var ButtonY = TriBaseYPos + 50;
    var CounterRect = paper.rect(ButtonX - 20, ButtonY - 540, 160, 30, 5, 5).attr({
        strokeWidth: 5,
        stroke: "black", strokeLinecap: "round", fill: "lightblue"
    });
    var CounterText = paper.text(ButtonX, ButtonY - 520, "Triangle: " + (Global_info.curPage + 1)).attr({fontsize: 50});
    var CounterButton = paper.g(CounterRect, CounterText);
    //
    // CounterButton.mouseover(function () {
    //     this.attr({cursor: 'pointer'});
    // });


    // var groupButton = paper.g(CounterRect, CounterText);
    // groupButton.mouseover(function () {
    //     this.attr({cursor: 'pointer'});
    // });





    /** Switches to the correct radio buttons. */
    var angleButton = document.getElementById('angleRadio');
    var vertexButton = document.getElementById('vertexRadio');
    angleButton.style.display ='none';
    vertexButton.style.display = 'none';

    if (QuestionType == 'vertex') {
        vertexButton.style.display = '';

    }
    else {
        angleButton.style.display = '';

    }
    /** Gets elements within the html id */
    var angleButtonElements = angleButton.getElementsByTagName('input');
    var vertexButtonElements = vertexButton.getElementsByTagName('input');

    /** Marker for if button was pushed  */
    var buttonMarker = false;

    // Continue button
    var ButtonPosX = TriBaseXEndOrig;
    var ButtonPosY = TriBaseYPos + 50;
    var NextButtonTxt = paper.text(ButtonPosX, ButtonPosY, "Continue").attr({fontsize: 50});
    var NextButtonRect = paper.rect(ButtonPosX - 20, ButtonPosY - 20, 120, 30, 5, 5).attr({
        strokeWidth: 5,
        stroke: "black", strokeLinecap: "round", fill: "lightblue"
    });
    var groupButton = paper.g(NextButtonRect, NextButtonTxt);
    groupButton.mouseover(function () {
        this.attr({cursor: 'pointer'});
    });
    groupButton.click(function () {
        // where I want to check that an option has been selected in radio buttons.
        for (i = 0; i < angleButtonElements.length; i++) {

            if (angleButtonElements[i].checked || vertexButtonElements[i].checked) {
                buttonMarker = true;
                if (angleButtonElements[i].checked) {
                    // put the answer in global user response
                    var logInfotemp = angleButtonElements[i].value;
                    //sendRequestPost('data', logInfo);
                }
                else {
                    // put answer in global user response
                    var logInfotemp = vertexButtonElements[i].value;
                    //sendRequestPost('data', logInfo);
                }
                break;
            }

        }


        if (buttonMarker == true) {
            Global_info.setup = 'Angle_' + Math.round(AngleOrig * 180 / Math.PI) + '_BaseFactor_' +
                BaseLengthFactor+'_Question_' + QuestionType + "_" + Question.slice(14, 22) + "_" + QuestionVariable
                + "_correctAnswer_" + Answer;
            Global_info.userResponse=logInfotemp;

            onNext();
        }
        else {
            alert('Please select an answer before continuing.');
        }




        //Measuring the time it took the subject to solve the last page
        // Global_info.end = Date.now();
        // var timeRound = Global_info.end - Global_info.start;
        // var logInfo = 'Run' + Global_info.curPage + '_' + Global_info.setup + '_Time_' + timeRound + '_UserResponse_'
        //     + Global_info.userResponse;
        // //alert(logInfo)
        // sendRequestPost('data', logInfo);
        // Global_info.start = Date.now();


        /** deselect all buttons */
        for (i = 0; i < angleButtonElements.length; i++) {
            angleButtonElements[i].checked = false;
            vertexButtonElements[i].checked = false;
        }

    });

};

function submit_demographis() {
    var gender = document.getElementById("gender").options[document.getElementById("gender").selectedIndex].value;
    var RightLeft = document.getElementById("RightLeft").options[document.getElementById("RightLeft").selectedIndex].value;
    var age = document.getElementById("age").value;
    var education = document.getElementById("education").value;

    if (gender == '' || age == '' || education == '') {
//		onContinue.curPage = onContinue.curPage-1;
        return false;
    }
    else {
        sendRequestPost('gender', gender);
        sendRequestPost('age', age);
        sendRequestPost('RightLeft', RightLeft);
        sendRequestPost('education', education);
        return true;
    }
};

function getCheckedRadio(radio_group_name) {
    radio_group = document.getElementsByName(radio_group_name);
    for (var i = 0; i < radio_group.length; i++) {
        var button = radio_group[i];
        if (button.checked) {
            return button.value;
        }
        ;
    }
    ;
    return "noAnswer";
};

function logAnswersSet1_basic() {
    var sol1_1 = document.getElementById("sol1_1").value;
    var sol1_2 = document.getElementById("sol1_2").value;
    var sol1_3 = document.getElementById("sol1_3").value;
    var sol1_4 = document.getElementById("sol1_4").value;
    sendRequestPost('sol1_1', sol1_1);
    sendRequestPost('sol1_2', sol1_2);
    sendRequestPost('sol1_3', sol1_3);
    sendRequestPost('sol1_4', sol1_4);
}

function submit_comments() {
    var comments = document.getElementById("endCommentsText").value;
    if (comments == "" || typeof comments == "undefined") {
        comments = "No comment";
    }

    sendRequestPost("EndComments", comments);
    Global_info.comments = 1;
    onNext();
};


function submit_consent() {
    var radio_group = "yesno2Experiment";
    var consent = getCheckedRadio(radio_group);
    if (consent == "no" || consent == "noAnswer") {
        return false;
    } else {
        sendRequestPost("yesno2Experiment", consent);
        return true;
    }
    ;
};

function onNextuserdata() {
    if (submit_demographis() == false) {
        alert('please provide the requested information');
    } else {
        onNext();
    }
    ;
};

function onNextConsentForm() {
    if (submit_consent() == false) {
        alert('If you wish to leave the study, please close the page. ' +
            '\n Otherwise, please check the consent button before proceeding');
    } else {
        Global_info.consent = 1;
        onNext();
    }
    ;
};

/** Button functions for triangle training. */

/** Shows how the distance between the angles increase when clicking on the button. */
function increaseDist() {
    Global_info.IncDist = 1;
    IncrDist();

}

/** Shows how the distance between the angles decreases when clicking on the button.  */
function decreaseDist() {
    Global_info.DecDist = 1;
    DecrDist();

}

/** Shows how the angle degree increases when clicking on the button. */
function increaseAngle() {
    Global_info.IncAng = 1;
    IncrAng();
}

/** Shows how the angle degree increases when clicking on the button. */
function decreaseAngle() {
    Global_info.DecAng = 1;
    DecrAng();
}

/** Goes back to the original triangle shown.  */
function originalButton() {
    OrigTri();
}

/** Simply a continue button for the triangle training. */
function ContinueButton() {
    if (Global_info.IncAng == 1 && Global_info.DecAng == 1 && Global_info.IncDist == 1 && Global_info.DecDist == 1) {
        Global_info.setup = 'TrainingTrial';
        onNext();
    }
    else {
        alert('Please click each button so we know you understand exactly what we mean.');
    }
};


//What to do when people press the Continue button (Instructions,Experiment,Thanks+ID)
function onNext() {
    //alert('onNext'+Global_info.curPage)
    $('.page').hide();

    // At the beginning of the experiment - Take demographics data
    if (typeof Global_info.curPage == 'undefined') {
        Global_info.curPage = -3;
        //blank all pages
        $(".page").hide();
        $("#user_data").show();
    }
    ;

    // Get consent from participants
    if (Global_info.curPage == -2) {
        //blank all pages
        $(".page").hide();
        $("#initial_instructions").show();
    }
    ;

    // Show participants instructions
    if (Global_info.curPage == -1 && Global_info.consent == 1) {
        //blank all pages
        $(".page").hide();
        trainTriangle();
        $("#TriangleTraining").show();
    }
    ;


    // Show participants the triangles
    //(Global_info.curPage<Global_info.TotRuns && Global_info.curPage>=0 && Global_info.consent==1)
    if ((Global_info.curPage < Global_info.TotRuns && Global_info.curPage >= 0 && Global_info.consent == 1)) {

        Global_info.sideRunNum = RunNumOrder[Global_info.curPage];
        Global_info.angleRunNum = RunNumOrder[Global_info.curPage];

        //Measuring the time it took the subject to solve the last page
        Global_info.end = Date.now();
        var timeRound = Global_info.end - Global_info.start;
        var logInfo = 'Run' + Global_info.curPage + '_' + Global_info.setup + '_Time_' + timeRound + '_UserResponse_'
            + Global_info.userResponse;
        //alert(logInfo)
        sendRequestPost('data', logInfo);
        Global_info.start = Date.now();

        //Preparing the next Triangle configuration
        drawTriangle();
        $('#TriangleManipulationPages').show();
    }
    ;

    // Get comments from the participants
    if (Global_info.curPage == Global_info.TotRuns && Global_info.comments == 0) {
        //Measuring the time it took the subject to solve the last page
        Global_info.end = Date.now();
        var timeRound = Global_info.end - Global_info.start;
        var logInfo = 'Run' + Global_info.curPage + '_' + Global_info.setup + '_Time_' + timeRound + '_UserResponse_'
            + Global_info.userResponse;
        sendRequestPost('data', logInfo);
        Global_info.userID = sendRequestPost('timeRound', timeRound);
        Global_info.start = Date.now();

        //Hide everything and show a thank you page
        $(".page").hide();
        $("#endComments.page").show();
    }
    ;

//	if (Global_info.curPage>=Global_info.TotRuns && Global_info.comments==1) {
    if (Global_info.curPage > Global_info.TotRuns) {
        //Measuring the time it took the subject to solve the last page
        Global_info.end = Date.now();
        var timeRound = Global_info.end - Global_info.start;
        Global_info.userID = sendRequestPost('timeRound', timeRound);
        Global_info.start = Date.now();

        //Hide everything and show a thank you page
        $(".page").hide();
        $("#ThankYou.page").show();
        $("#thanks").text('Thank you for your participation.');
        $("#userID").text('Your Validation code is: ' + Global_info.userID);
    }
    ;
    Global_info.curPage++;
};

$(document).ready(function () {
    // At beginning - show instructions page
    $('.page').hide();
    $('#ConsentForm').show();
});
