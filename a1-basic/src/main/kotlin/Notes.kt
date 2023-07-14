import javafx.application.Application
import javafx.beans.value.ChangeListener
import javafx.collections.FXCollections
import javafx.event.EventHandler
import javafx.geometry.*
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.text.Text
import javafx.stage.Stage


class Notes : Application() {
    val WINDOW_NAME = "CS349 - A1 Notes - g3shao"
    val STAGE_WIDTH = 800.0
    val STAGE_HEIGHT = 600.0
    val STAGE_MIN_WIDTH = 640.0
    val STAGE_MIN_HEIGHT = 480.0
    var notesList = HashMap<String, Boolean>()
    val TOOLTIP_PADDING = Insets(10.0, 10.0, 10.0, 10.0)

    val ascCompartor = Comparator<String>{o1:String, o2:String ->
        when {
            (o1.length - o2.length) > 0 -> 1
            (o1.length - o2.length) < 0 -> -1
            else -> (o1.compareTo(o2))
        }
    }
    val descComparator = Comparator<String>{o1:String, o2:String ->
        when {
            (o1.length - o2.length) > 0 -> -1
            (o1.length - o2.length) < 0 -> 1
            else -> (o2.compareTo(o1))
        }
    }
    var listView = ScrollPane()
    var orderChoice = ChoiceBox<String>()
    var pane = BorderPane()
    var archiveCheck = CheckBox()
    var isList = true
    var text = Text()
    override fun start(stage: Stage) {

        // construct notes list
        notesList.put("Sample Acive Note 1", true)
        notesList.put("Sample Archived Note 1", false)
        notesList.put("Sample Short", true)
        notesList.put("This is a very very very very very very very very very very very very very long notes that is active", true)

        pane = BorderPane()

        // create tooltips
        val realToolTips = BorderPane()
        val tooltips = HBox()

        val viewGroup = HBox(10.0)
        val viewLable = Label("View:")
        val listButton = Button("List")
        val gridButton = Button("Grid")
        listButton.isDisable = true
        listButton.onMouseClicked = EventHandler {
            isList = true
            listButton.isDisable = true
            gridButton.isDisable = false
            renderNotes(notesList)
        }
        gridButton.onMouseClicked = EventHandler {
            isList = false
            listButton.isDisable = false
            gridButton.isDisable = true
            renderNotes(notesList)
        }

        val separator = Separator()
        separator.orientation = Orientation.VERTICAL
        separator.valignment = VPos.CENTER
        val separator1 = Separator()
        separator1.orientation = Orientation.VERTICAL
        separator1.valignment = VPos.CENTER
        val separator2 = Separator()
        separator2.orientation = Orientation.HORIZONTAL
        separator2.valignment = VPos.CENTER
        val separator3 = Separator()
        separator3.orientation = Orientation.HORIZONTAL
        separator3.valignment = VPos.CENTER

        val archiveGroup = HBox(10.0)
        val archiveLable = Label("Show archived:")
        archiveCheck = CheckBox()
        archiveGroup.children.addAll(archiveLable, archiveCheck)

        val orderGroup = HBox(10.0)
        val orderLabel = Label("Order by:")
        val st = listOf("Length (asc)", "Length (desc)")
        orderChoice = ChoiceBox(FXCollections.observableArrayList(st))
        orderChoice.selectionModel.select(0)
        orderGroup.children.addAll(orderLabel, orderChoice)

        val clearGroup = HBox(10.0)
        val clearButton = Button("Clear")
        clearButton.onMouseClicked = EventHandler {
            notesList.clear()
            renderNotes(notesList)
        }
        clearGroup.children.addAll(clearButton)

        // set paddings
        viewLable.padding = Insets(4.0, 0.0, 0.0, 0.0)
        archiveLable.padding = Insets(4.0, 0.0, 0.0, 0.0)
        orderLabel.padding = Insets(4.0, 0.0, 0.0, 0.0)
        archiveCheck.padding = Insets(4.0, 0.0, 0.0, 0.0)
        orderChoice.padding = Insets(2.0, 0.0, 0.0, 0.0)
        listButton.prefWidth = 50.0
        gridButton.prefWidth = 50.0
        clearButton.prefWidth = 50.0
        viewGroup.padding = TOOLTIP_PADDING
        archiveGroup.padding = TOOLTIP_PADDING
        orderGroup.padding = TOOLTIP_PADDING
        clearGroup.padding = TOOLTIP_PADDING
        viewGroup.children.addAll(viewLable, listButton, gridButton)
        tooltips.children.addAll(viewGroup, separator, archiveGroup, separator1, orderGroup)
        realToolTips.left = tooltips
        realToolTips.right = clearGroup
        realToolTips.bottom = separator2
        pane.top = realToolTips
        orderChoice.selectionModel.selectedItemProperty()
            .addListener(ChangeListener<String?> { _,_,_ ->
                renderNotes(notesList)
            })
        archiveCheck.onMouseClicked = EventHandler {
            renderNotes(notesList)
        }
        renderNotes(notesList)
        pane.bottom = VBox(separator3, VBox(text))
        // display the pane
        val scene = Scene(pane)
        stage.title = WINDOW_NAME
        stage.minWidth = STAGE_MIN_WIDTH
        stage.minHeight = STAGE_MIN_HEIGHT
        stage.scene = scene
        stage.width = STAGE_WIDTH
        stage.height = STAGE_HEIGHT
        stage.show()
    }
    fun renderNotes(notes: Map<String, Boolean>) {
        if (isList) {
            renderListView(notes)
        } else {
            renderGridView(notes)
        }
        updateStatus(notes)
    }

    fun renderGridView(notes: Map<String, Boolean>) {
        var comp = ascCompartor
        if (orderChoice.selectionModel.selectedItem == "Length (desc)") {
            comp = descComparator
        }
        val sorted = notes.toSortedMap(comp)

        val listView1 = FlowPane(Orientation.HORIZONTAL)
        listView1.vgap = 20.0
        listView1.hgap = 20.0
        listView1.children.clear()

        val specialNote = createGridSpecialNote()
        listView1.children.addAll(specialNote)
        listView1.padding = TOOLTIP_PADDING
        for (s in sorted.entries.iterator()) {
            val note = s.key
            val active = s.value

            val notesBox = createGridNoteBox(note, active)
            if (archiveCheck.isSelected || active) {
                listView1.children.add(notesBox)
            }
        }
        listView = ScrollPane(listView1)
        listView.padding = TOOLTIP_PADDING
        listView.isFitToWidth = true
        listView.setBackground(
            Background(BackgroundFill(Color.TRANSPARENT, null, null))
        )
        pane.center = listView
    }

    fun createGridSpecialNote(): VBox {
        // Special Notes: Input
        val specialNote = VBox(10.0)
        val specialNoteInput = TextArea()
        specialNoteInput.maxHeight = 150.0
        specialNoteInput.maxWidth = 185.0
        specialNoteInput.minWidth = 185.0
        specialNoteInput.minHeight = 150.0
        specialNoteInput.isWrapText = true
        val specialNoteButton = Button("Create")
        specialNoteButton.isDisable = true
        specialNote.children.addAll(specialNoteInput, specialNoteButton)
        specialNote.padding = TOOLTIP_PADDING
        specialNote.maxHeight = 205.0
        specialNote.maxWidth = 205.0
        specialNote.minWidth = 205.0
        specialNote.minHeight = 205.0

        specialNoteButton.prefHeight = 42.0
        specialNoteButton.prefWidth = 205.0
        specialNoteButton.onMouseClicked = EventHandler {
            if (specialNoteInput.text.length > 0) {
                val targetNote = specialNoteInput.text
                val active = true
                notesList.put(targetNote, active)
                renderNotes(notesList)
            }
        }
        specialNoteInput.onKeyTyped = EventHandler {
            if (specialNoteInput.text.length > 0) {
                specialNoteButton.isDisable = false
            } else {
                specialNoteButton.isDisable = true
            }
        }
        specialNote.background = Background(
            BackgroundFill(
                Color.SALMON, CornerRadii(10.0), Insets.EMPTY
            )
        )
        specialNote.alignment = Pos.CENTER_RIGHT
        return specialNote
    }
    fun renderListView(notes: Map<String, Boolean>) {
        var comp = ascCompartor
        if (orderChoice.selectionModel.selectedItem == "Length (desc)") {
            comp = descComparator
        }
        val sorted = notes.toSortedMap(comp)

        val listView1 = VBox(10.0)
        listView1.children.clear()

        val specialNote = createSpecialNote()
        listView1.children.addAll(specialNote)
        listView1.padding = TOOLTIP_PADDING

        for (s in sorted.entries.iterator()) {
            val note = s.key
            val active = s.value

            val notesBox = createNoteBox(note, active)
            if (archiveCheck.isSelected || active) {
                listView1.children.add(notesBox)
            }
        }
        listView = ScrollPane(listView1)
        listView.padding = TOOLTIP_PADDING
        listView.isFitToWidth = true
        listView.setBackground(
            Background(BackgroundFill(Color.TRANSPARENT, null, null))
        )
        pane.center = listView
    }
    fun createSpecialNote():HBox {
        // Special Notes: Input
        val specialNote = HBox(10.0)
        val specialNoteInput = TextArea()
        specialNoteInput.isWrapText = true
        val specialNoteButton = Button("Create")
        specialNoteButton.isDisable = true
        specialNote.children.addAll(specialNoteInput, specialNoteButton)
        specialNote.padding = TOOLTIP_PADDING
        specialNote.prefHeight = 62.0

        specialNoteButton.prefHeight = 42.0
        specialNoteButton.minWidth = 75.0
        specialNoteButton.onMouseClicked = EventHandler {
            if (specialNoteInput.text.length > 0) {
                val targetNote = specialNoteInput.text
                val active = true
                notesList.put(targetNote, active)
                renderNotes(notesList)
            }
        }
        specialNoteInput.onKeyTyped = EventHandler {
            if (specialNoteInput.text.length > 0) {
                specialNoteButton.isDisable = false
            } else {
                specialNoteButton.isDisable = true
            }
        }
        specialNote.background = Background(
            BackgroundFill(
                Color.SALMON, CornerRadii(10.0), Insets.EMPTY
            )
        )
        specialNote.alignment = Pos.CENTER_RIGHT
        HBox.setHgrow(specialNoteInput, Priority.ALWAYS)
        return specialNote
    }
    fun createNoteBox(note:String, active:Boolean):HBox {
        val notesBox = HBox(10.0)
        val notesBoxInput = Label(note)
        notesBoxInput.isWrapText = true
        val notesBoxinput = HBox(notesBoxInput)

        val notesBoxButton = CheckBox()
        val archiveLabel = Label("Archived")
        val archiveBox = HBox( 10.0, notesBoxButton, archiveLabel)
        archiveBox.minWidth = 85.0
        notesBox.children.addAll(notesBoxinput, archiveBox)
        HBox.setHgrow(notesBoxinput, Priority.ALWAYS)
        notesBox.padding = TOOLTIP_PADDING
        var color = Color.LIGHTYELLOW
        if (!active) {
            color = Color.LIGHTGRAY
            notesBoxButton.isSelected = true
        }
        notesBox.background = Background(
            BackgroundFill(
                color, CornerRadii(10.0), Insets.EMPTY
            )
        )

        notesBoxButton.onMouseClicked = EventHandler {
            if (notesBoxButton.isSelected) {
                notesList.put(note, false)
            } else {
                notesList.put(note, true)
            }
            renderNotes(notesList)
        }
        return notesBox
    }

    fun createGridNoteBox(note:String, active:Boolean):VBox {
        val notesBox = VBox(10.0)
        val notesBoxInput = Label(note)
        notesBoxInput.isWrapText = true
        val notesBoxinput = HBox(notesBoxInput)
        notesBoxinput.maxHeight = 180.0
        notesBoxinput.maxWidth = 180.0
        notesBox.maxWidth = 205.0
        notesBox.maxHeight = 205.0
        notesBox.minWidth = 205.0
        notesBox.minHeight = 205.0
        val notesBoxButton = CheckBox()
        val archiveLabel = Label("Archived")
        val archiveBox = HBox( 10.0, notesBoxButton, archiveLabel)
        archiveBox.minWidth = 60.0
        notesBox.children.addAll(notesBoxinput, archiveBox)
        VBox.setVgrow(notesBoxinput, Priority.ALWAYS)
        notesBox.padding = TOOLTIP_PADDING
        notesBox.prefHeight = 62.0
        var color = Color.LIGHTYELLOW
        if (!active) {
            color = Color.LIGHTGRAY
            notesBoxButton.isSelected = true
        }
        notesBox.background = Background(
            BackgroundFill(
                color, CornerRadii(10.0), Insets.EMPTY
            )
        )

        notesBoxButton.onMouseClicked = EventHandler {
            if (notesBoxButton.isSelected) {
                notesList.put(note, false)
            } else {
                notesList.put(note, true)
            }
            renderNotes(notesList)
        }
        return notesBox
    }

    fun updateStatus(notesList: Map<String, Boolean>) {
        var activeCnt = 0
        for (s in notesList.entries.iterator()) {
            if (s.value == true) {
                activeCnt += 1
            }
        }
        if (notesList.size == 1) {
            if (activeCnt == 1) {
                text.text = "${notesList.size} note, ${activeCnt} of which is active"
            } else {
                text.text = "${notesList.size} note, ${activeCnt} of which are active"
            }
        } else {
            if (activeCnt == 1) {
                text.text = "${notesList.size} notes, ${activeCnt} of which is active"
            } else {
                text.text = "${notesList.size} notes, ${activeCnt} of which are active"
            }
        }
    }
}