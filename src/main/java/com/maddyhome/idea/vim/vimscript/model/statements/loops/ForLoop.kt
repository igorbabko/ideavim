package com.maddyhome.idea.vim.vimscript.model.statements.loops

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.Editor
import com.maddyhome.idea.vim.ex.ExException
import com.maddyhome.idea.vim.vimscript.model.Executable
import com.maddyhome.idea.vim.vimscript.model.ExecutionResult
import com.maddyhome.idea.vim.vimscript.model.datatypes.VimBlob
import com.maddyhome.idea.vim.vimscript.model.datatypes.VimDataType
import com.maddyhome.idea.vim.vimscript.model.datatypes.VimList
import com.maddyhome.idea.vim.vimscript.model.datatypes.VimString
import com.maddyhome.idea.vim.vimscript.model.expressions.Expression
import com.maddyhome.idea.vim.vimscript.model.expressions.Variable
import com.maddyhome.idea.vim.vimscript.services.VariableService

// todo refactor us senpai :(
data class ForLoop(val variable: Variable, val iterable: Expression, val body: List<Executable>) : Executable {
  override lateinit var parent: Executable

  override fun execute(editor: Editor, context: DataContext): ExecutionResult {
    var result: ExecutionResult = ExecutionResult.Success
    body.forEach { it.parent = this }

    var iterableValue = iterable.evaluate(editor, context, this)
    if (iterableValue is VimString) {
      for (i in iterableValue.value) {
        VariableService.storeVariable(variable, VimString(i.toString()), editor, context, this)
        for (statement in body) {
          if (result is ExecutionResult.Success) {
            result = statement.execute(editor, context)
          } else {
            break
          }
        }
        if (result is ExecutionResult.Break) {
          result = ExecutionResult.Success
          break
        } else if (result is ExecutionResult.Continue) {
          result = ExecutionResult.Success
          continue
        }
      }
    } else if (iterableValue is VimList) {
      var index = 0
      while (index < (iterableValue as VimList).values.size) {
        VariableService.storeVariable(variable, iterableValue.values[index], editor, context, this)
        for (statement in body) {
          if (result is ExecutionResult.Success) {
            result = statement.execute(editor, context)
          } else {
            break
          }
        }
        if (result is ExecutionResult.Break) {
          result = ExecutionResult.Success
          break
        } else if (result is ExecutionResult.Continue) {
          result = ExecutionResult.Success
          continue
        }
        index += 1
        iterableValue = iterable.evaluate(editor, context, this) as VimList
      }
    } else if (iterableValue is VimBlob) {
      TODO("Not yet implemented")
    } else {
      throw ExException("E1098: String, List or Blob required")
    }
    return result
  }
}

data class ForLoopWithList(val variables: List<String>, val iterable: Expression, val body: List<Executable>) : Executable {
  override lateinit var parent: Executable

  override fun execute(editor: Editor, context: DataContext): ExecutionResult {
    var result: ExecutionResult = ExecutionResult.Success
    body.forEach { it.parent = this }

    var iterableValue = iterable.evaluate(editor, context, this)
    if (iterableValue is VimList) {
      var index = 0
      while (index < (iterableValue as VimList).values.size) {
        storeListVariables(iterableValue.values[index], editor, context)
        for (statement in body) {
          if (result is ExecutionResult.Success) {
            result = statement.execute(editor, context)
          } else {
            break
          }
        }
        if (result is ExecutionResult.Break) {
          result = ExecutionResult.Success
          break
        } else if (result is ExecutionResult.Continue) {
          result = ExecutionResult.Success
          continue
        }
        index += 1
        iterableValue = iterable.evaluate(editor, context, this) as VimList
      }
    } else {
      throw ExException("E714: List required")
    }
    return result
  }

  private fun storeListVariables(list: VimDataType, editor: Editor, context: DataContext) {
    if (list !is VimList) {
      throw ExException("E714: List required")
    }

    if (list.values.size < variables.size) {
      throw ExException("E688: More targets than List items")
    }
    if (list.values.size > variables.size) {
      throw ExException("E684: Less targets than List items")
    }

    for (item in list.values.withIndex()) {
      VariableService.storeVariable(Variable(null, variables[item.index]), item.value, editor, context, this)
    }
  }
}